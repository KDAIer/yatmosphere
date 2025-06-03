package com.example.library.service.impl;

import com.example.library.common.service.impl.BaseServiceImpl;
import com.example.library.etc.ServiceException;
import com.example.library.mapper.AirconMapper;
import com.example.library.mapper.DeviceMapper;
import com.example.library.pojo.entity.Aircon;
import com.example.library.pojo.entity.Device;
import com.example.library.pojo.vo.Result;
import com.example.library.service.AirconService;
import com.example.library.service.DeviceService;
import jakarta.annotation.Resource;
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
        return updated > 0;
    }
    @Override
    @Transactional
    public boolean wind1(String deviceName) {
        System.out.println("deviceName:" + deviceName);
        int updated = airconMapper.wind1(deviceName);
        return updated > 0;
    }
    @Override
    @Transactional
    public boolean wind2(String deviceName) {
        System.out.println("deviceName:" + deviceName);
        int updated = airconMapper.wind2(deviceName);
        return updated > 0;
    }


    @Override
    @Transactional
    public boolean wind3(String deviceName) {
        System.out.println("deviceName:" + deviceName);
        int updated = airconMapper.wind3(deviceName);
        return updated > 0;
    }
    @Override
    @Transactional
    public boolean wind4(String deviceName) {
        System.out.println("deviceName:" + deviceName);
        int updated = airconMapper.wind4(deviceName);
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
    public boolean turnofflight(String deviceName) {
        System.out.println("deviceName:" + deviceName);
        int updated = airconMapper.turnofflight(deviceName);
        return updated > 0;
    }


    @Override
    @Transactional
    public boolean wind5(String deviceName) {
        System.out.println("deviceName:" + deviceName);
        int updated = airconMapper.wind5(deviceName);
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