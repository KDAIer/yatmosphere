package com.example.library.service;

import com.example.library.common.service.BaseService;
import com.example.library.pojo.entity.Light;
import java.util.List;

public interface LightService extends BaseService<Light> {
    List<Light> getAllLights();

    boolean addLight(Light light);
    boolean turnOnLight(String deviceName);
    boolean turnOffLight(String deviceName);
    boolean brightness(String deviceName, int brightness);
    boolean naturelight(String deviceName);
    boolean warmlight(String deviceName);
    boolean coldlight(String deviceName);
}