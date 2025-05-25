package com.example.library.service.impl;

import com.example.library.common.service.impl.BaseServiceImpl;
import com.example.library.mapper.LightMapper;
import com.example.library.pojo.entity.Light;
import com.example.library.service.LightService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LightServiceImpl extends BaseServiceImpl<Light, LightMapper> implements LightService {
    @Resource
    private LightMapper lightMapper;

    @Override
    public List<Light> getAllLights() {
        return lightMapper.selectAllLights();
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