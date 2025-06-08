package com.example.library.service.impl;

import com.example.library.common.service.impl.BaseServiceImpl;
import com.example.library.controller.DeviceControlController;
import com.example.library.etc.ServiceException;
import com.example.library.mapper.AirconMapper;
import com.example.library.mapper.DeviceMapper;
import com.example.library.pojo.entity.Aircon;
import com.example.library.pojo.entity.Device;
import com.example.library.pojo.vo.Result;
import com.example.library.service.AirconService;
import com.example.library.service.DeviceService;
import com.example.library.service.MqttControlService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AirconServiceImpl extends BaseServiceImpl<Aircon, AirconMapper> implements AirconService {
    @Resource
    private AirconMapper airconMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Autowired
    private MqttControlService mqttControlService;


    @Override
    public List<Aircon> getAllAircons() {
        return airconMapper.selectAllAircons();
    }

    @Override
    @Transactional
    public boolean desenergy(String deviceName) {
        System.out.println("deviceName:" + deviceName);
        int updated = airconMapper.desenergymodeByDeviceName(deviceName);
//        Aircon aircon = airconMapper.selectTempAndModeByDeviceName(deviceName);
//        // 如果查询结果不为空
//        if (aircon != null) {
//            // 格式化温度和模式
//            String detail = formatDetail(aircon.getTemperature(), aircon.getMode());
//            // 更新设备详情
//            airconMapper.updateDeviceDetail(deviceName, detail);
//        }
        // 发送mqtt消息
        try {
            String deviceId = airconMapper.selectDeviceIdByDeviceName(deviceName);
            DeviceControlController.ControlRequest request = new DeviceControlController.ControlRequest();
            request.setmsg_id(System.currentTimeMillis());
            request.setdevice_id(deviceId);
            request.setdevice_type("air_conditioner");
            request.setAction("set_eco");
            request.setValue("on");
            mqttControlService.sendCommand(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("发送MQTT命令失败: " + e.getMessage());
        }
        // 返回更新结果
        return updated > 0;
    }

    @Override
    @Transactional
    public boolean turnoffdesenergy(String deviceName) {
        System.out.println("deviceName:" + deviceName);
        int updated = airconMapper.turnoffdesenergymodeByDeviceName(deviceName);
        // 查询当前温度和原detail
//        Aircon aircon = airconMapper.selectTempAndModeByDeviceName(deviceName);
//        String oldDetail = aircon.getDetail();
//        double newTemp = aircon.getTemperature();
//        String newDetail = updateDetailTemperature(oldDetail, newTemp);
//        airconMapper.updateDeviceDetail(deviceName, newDetail);
        // 发送mqtt消息
        try {
            String deviceId = airconMapper.selectDeviceIdByDeviceName(deviceName);
            DeviceControlController.ControlRequest request = new DeviceControlController.ControlRequest();
            request.setmsg_id(System.currentTimeMillis());
            request.setdevice_id(deviceId);
            request.setdevice_type("air_conditioner");
            request.setAction("set_eco");
            request.setValue("off");
            mqttControlService.sendCommand(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("发送MQTT命令失败: " + e.getMessage());
        }
        return updated > 0;
    }

    @Override
    @Transactional
    public boolean setWindLevel(String deviceName, int level) {
        // 验证风量等级是否有效
        if (level < 1 || level > 5) {
            throw new IllegalArgumentException("风量等级必须在 1-5 范围内");
        }
        // 发送 MQTT 命令
        try {
            String deviceId = airconMapper.selectDeviceIdByDeviceName(deviceName);
            DeviceControlController.ControlRequest request = new DeviceControlController.ControlRequest();
            request.setmsg_id(System.currentTimeMillis());
            request.setdevice_id(deviceId);
            request.setdevice_type("air_conditioner");
            request.setAction("set_fan_level");
            request.setValue(level);
            mqttControlService.sendCommand(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("发送MQTT命令失败: " + e.getMessage());
        }
        System.out.println("deviceName:" + deviceName + ", level:" + level);
        int updated = airconMapper.setWindLevel(deviceName, level);
        return updated > 0;
    }

    @Override
    @Transactional
    public boolean turnonlight(String deviceName) {
        System.out.println("deviceName:" + deviceName);
        int updated = airconMapper.turnonlight(deviceName);
        return updated > 0;
    }
    @Override
    @Transactional
    public boolean updateAirconMode(String deviceId, String mode) {
        // 根据 deviceId 查询空调记录（包括温度）
        Aircon aircon = airconMapper.selectTempAndModeByDeviceId(deviceId);
        if (aircon == null) {
            throw new ServiceException("未找到指定设备: " + deviceId);
        }
        // 计算新的detail，通过已有的formatDetail方法
        String newDetail = formatDetail(aircon.getTemperature(), mode);
        // 更新空调mode和设备描述detail
        int updated = airconMapper.updateModeByDeviceId(deviceId, mode, newDetail);
        // 发送 MQTT 命令
        try {
            DeviceControlController.ControlRequest request = new DeviceControlController.ControlRequest();
            request.setmsg_id(System.currentTimeMillis());
            request.setdevice_id(deviceId);
            request.setdevice_type("air_conditioner");
            request.setAction("set_mode");
            request.setValue(mode);
            mqttControlService.sendCommand(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("发送MQTT命令失败: " + e.getMessage());
        }
        return updated > 0;
    }

    @Override
    @Transactional
    public boolean updateAirconPower(String deviceId, int status) {
        int updatedAircon = airconMapper.updatePowerByDeviceId(deviceId, status);
        int updatedDevice = deviceMapper.updateStatusByDeviceId(deviceId, status);
        // 发送 MQTT 命令
        try {
            DeviceControlController.ControlRequest request = new DeviceControlController.ControlRequest();
            request.setmsg_id(System.currentTimeMillis());
            request.setdevice_id(deviceId);
            request.setdevice_type("air_conditioner");
            request.setAction("set_power");
            request.setValue(status == 1 ? "on" : "off");
            mqttControlService.sendCommand(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("发送MQTT命令失败: " + e.getMessage());
        }
        return updatedAircon > 0 && updatedDevice > 0;
    }

    @Override
    @Transactional
    public boolean turnofflight(String deviceName) {
        System.out.println("deviceName:" + deviceName);
        int updated = airconMapper.turnofflight(deviceName);
        return updated > 0;
    }



    @Override
    @Transactional
    public boolean addAircon(Aircon aircon) {
        // 先查deviceId是否已存在
        Device exist = deviceMapper.selectById(aircon.getDeviceId());
        if (exist != null) {
            throw new ServiceException("设备ID已存在，不能重复添加！");
        }
        // 先插入device表
        Device device = new Device();
        device.setDeviceId(aircon.getDeviceId());
        device.setDeviceName(aircon.getDeviceName());
        device.setCategory(aircon.getCategory());
        device.setStatus(aircon.getStatus());
        device.setDetail(aircon.getDetail());
        int deviceResult = deviceMapper.insertDevice(device);

        // 再插入aircon表
        int airconResult = airconMapper.insertAircon(aircon);

        // 发送MQTT命令
        try {
            mqttControlService.createDevice(aircon.getDeviceId(), "air_conditioner");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("发送MQTT命令失败: " + e.getMessage());
        }
        return deviceResult > 0 && airconResult > 0;
    }

    @Override
    @Transactional
    public boolean increaseTemperature(String deviceName) {
        System.out.println("deviceName:" + deviceName);
        int updated = airconMapper.increaseTemperatureByDeviceName(deviceName);
        // 查询当前温度和原detail
        Aircon aircon = airconMapper.selectTempAndModeByDeviceName(deviceName);
        String oldDetail = aircon.getDetail();
        double newTemp = aircon.getTemperature();
        String newDetail = updateDetailTemperature(oldDetail, newTemp);
        airconMapper.updateDeviceDetail(deviceName, newDetail);
        // 发送mqtt消息
        try {
            String deviceId = airconMapper.selectDeviceIdByDeviceName(deviceName);
            DeviceControlController.ControlRequest request = new DeviceControlController.ControlRequest();
            request.setmsg_id(System.currentTimeMillis());
            request.setdevice_id(deviceId);
            request.setdevice_type("air_conditioner");
            request.setAction("set_temperature");
            request.setValue(newTemp);
            mqttControlService.sendCommand(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("发送MQTT命令失败: " + e.getMessage());
        }
        return updated > 0;
    }

    @Override
    @Transactional
    public boolean decreaseTemperature(String deviceName) {
        // 根据设备名称减少温度
        int updated = airconMapper.decreaseTemperatureByDeviceName(deviceName);
        // 根据设备名称查询温度和模式
        Aircon aircon = airconMapper.selectTempAndModeByDeviceName(deviceName);
        // 如果查询结果不为空
        if (aircon != null) {
            // 格式化温度和模式
            String detail = formatDetail(aircon.getTemperature(), aircon.getMode());
            // 更新设备详情
            airconMapper.updateDeviceDetail(deviceName, detail);
        }
        // 发送mqtt消息
        try {
            String deviceId = airconMapper.selectDeviceIdByDeviceName(deviceName);
            DeviceControlController.ControlRequest request = new DeviceControlController.ControlRequest();
            request.setmsg_id(System.currentTimeMillis());
            request.setdevice_id(deviceId);
            request.setdevice_type("air_conditioner");
            request.setAction("set_temperature");
            request.setValue(aircon.getTemperature());
            mqttControlService.sendCommand(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("发送MQTT命令失败: " + e.getMessage());
        }
        // 返回更新结果
        return updated > 0;
    }
    @Override
    public boolean refrigerationmod(String deviceName){

        int updated = airconMapper.refrigerationmod(deviceName);
        Aircon aircon = airconMapper.selectTempAndModeByDeviceName(deviceName);
        // 如果查询结果不为空
        if (aircon != null) {
            // 格式化温度和模式
            String detail = formatDetail(aircon.getTemperature(), aircon.getMode());
            // 更新设备详情
            airconMapper.updateDeviceDetail(deviceName, detail);
        }
        // 发送mqtt消息
        try {
            String deviceId = airconMapper.selectDeviceIdByDeviceName(deviceName);
            DeviceControlController.ControlRequest request = new DeviceControlController.ControlRequest();
            request.setmsg_id(System.currentTimeMillis());
            request.setdevice_id(deviceId);
            request.setdevice_type("air_conditioner");
            request.setAction("set_mode");
            request.setValue("制冷");
            mqttControlService.sendCommand(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("发送MQTT命令失败: " + e.getMessage());
        }
        // 返回更新结果
        return updated > 0;

    }
    @Override
    public boolean heatingmod(String deviceName){

        int updated =  airconMapper.heatingmod(deviceName);

        Aircon aircon = airconMapper.selectTempAndModeByDeviceName(deviceName);
        // 如果查询结果不为空
        if (aircon != null) {
            // 格式化温度和模式
            String detail = formatDetail(aircon.getTemperature(), aircon.getMode());
            // 更新设备详情
            airconMapper.updateDeviceDetail(deviceName, detail);
        }
        // 发送mqtt消息
        try {
            String deviceId = airconMapper.selectDeviceIdByDeviceName(deviceName);
            DeviceControlController.ControlRequest request = new DeviceControlController.ControlRequest();
            request.setmsg_id(System.currentTimeMillis());
            request.setdevice_id(deviceId);
            request.setdevice_type("air_conditioner");
            request.setAction("set_mode");
            request.setValue("制热");
            mqttControlService.sendCommand(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("发送MQTT命令失败: " + e.getMessage());
        }
        // 返回更新结果
        return updated > 0;
    }
    @Override
    public boolean dehumidifiermod(String deviceName){
        int updated =  airconMapper.dehumidifiermod(deviceName);
        Aircon aircon = airconMapper.selectTempAndModeByDeviceName(deviceName);
        // 如果查询结果不为空
        if (aircon != null) {
            // 格式化温度和模式
            String detail = formatDetail(aircon.getTemperature(), aircon.getMode());
            // 更新设备详情
            airconMapper.updateDeviceDetail(deviceName, detail);
        }
        // 发送mqtt消息
        try {
            String deviceId = airconMapper.selectDeviceIdByDeviceName(deviceName);
            DeviceControlController.ControlRequest request = new DeviceControlController.ControlRequest();
            request.setmsg_id(System.currentTimeMillis());
            request.setdevice_id(deviceId);
            request.setdevice_type("air_conditioner");
            request.setAction("set_mode");
            request.setValue("除湿");
            mqttControlService.sendCommand(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("发送MQTT命令失败: " + e.getMessage());
        }
        // 返回更新结果
        return updated > 0;
    }
    @Override
    public boolean blowmod(String deviceName){
        int updated =  airconMapper.blowmod(deviceName);
        Aircon aircon = airconMapper.selectTempAndModeByDeviceName(deviceName);
        // 如果查询结果不为空
        if (aircon != null) {
            // 格式化温度和模式
            String detail = formatDetail(aircon.getTemperature(), aircon.getMode());
            // 更新设备详情
            airconMapper.updateDeviceDetail(deviceName, detail);
        }
        // 发送mqtt消息
        try {
            String deviceId = airconMapper.selectDeviceIdByDeviceName(deviceName);
            DeviceControlController.ControlRequest request = new DeviceControlController.ControlRequest();
            request.setmsg_id(System.currentTimeMillis());
            request.setdevice_id(deviceId);
            request.setdevice_type("air_conditioner");
            request.setAction("set_mode");
            request.setValue("送风");
            mqttControlService.sendCommand(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("发送MQTT命令失败: " + e.getMessage());
        }
        // 返回更新结果
        return updated > 0;
    }

    private String formatDetail(Double temperature, String mode) {
        // 只保留整数部分
        System.out.println("formatDetail temperature = " + temperature + ", mode = " + mode);
        DecimalFormat df = new DecimalFormat("#.0");
        return df.format(temperature) + "℃ " + mode + "模式";
    }

    private String updateDetailTemperature(String detail, double newTemperature) {
        // 匹配以数字开头，后跟“℃”
        Pattern pattern = Pattern.compile("^[\\d.]+(?=℃)");
        Matcher matcher = pattern.matcher(detail);
        if (matcher.find()) {
            // 替换为新温度（保留1位小数）
            String newTempStr = String.format("%.1f", newTemperature);
            return matcher.replaceFirst(newTempStr);
        }
        // 如果没有匹配，直接返回原字符串
        return detail;
    }

}