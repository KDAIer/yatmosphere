package com.example.library.service.impl;

import com.example.library.common.service.impl.BaseServiceImpl;
import com.example.library.mapper.LightMapper;
import com.example.library.pojo.entity.Light;
import com.example.library.service.LightService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.example.library.mapper.DeviceMapper;
import com.example.library.pojo.entity.Device;
@Service
public class LightServiceImpl extends BaseServiceImpl<Light, LightMapper> implements LightService {
    @Resource
    private LightMapper lightMapper;

    @Override
    public List<Light> getAllLights() {
        return lightMapper.selectAllLights();
    }


//import org.springframework.transaction.annotation.Transactional;
// ...existing code...

    @Resource
    private DeviceMapper deviceMapper;

    @Override
    @Transactional
    public boolean addLight(Light light) {
        // 检查 deviceId 是否已存在
        Device exist = deviceMapper.selectById(light.getDeviceId());
        if (exist != null) {
            throw new RuntimeException("设备ID已存在，不能重复添加！");
        }
        // 插入 device 表
        Device device = new Device();
        device.setDeviceId(light.getDeviceId());
        device.setDeviceName(light.getDeviceName());
        device.setCategory(light.getCategory());
        device.setStatus(light.getStatus());
        device.setDetail(light.getDetail());
        int deviceResult = deviceMapper.insertDevice(device);

        // 插入 light 表
        int lightResult = lightMapper.insertLight(light);

        return deviceResult > 0 && lightResult > 0;
    }

    @Override
    @Transactional
    public boolean turnOnLight(String deviceName) {
        return lightMapper.turnOnLight(deviceName) > 0;
    }

    @Override
    @Transactional
    public boolean turnOffLight(String deviceName) {
        return lightMapper.turnOffLight(deviceName) > 0;
    }
}