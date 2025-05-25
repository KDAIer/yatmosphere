package com.example.library.service;

import com.example.library.common.service.BaseService;
import com.example.library.pojo.entity.Light;
import java.util.List;

public interface LightService extends BaseService<Light> {
    List<Light> getAllLights();
    boolean turnOnLight(String deviceName);
    boolean turnOffLight(String deviceName);
}