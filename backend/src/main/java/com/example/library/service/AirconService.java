package com.example.library.service;

import com.example.library.common.service.BaseService;
import com.example.library.pojo.entity.Aircon;
import java.util.List;

public interface AirconService extends BaseService<Aircon> {
    List<Aircon> getAllAircons();
    boolean increaseTemperature(String deviceName);
    boolean decreaseTemperature(String deviceName);

    boolean addAircon(Aircon aircon);
}