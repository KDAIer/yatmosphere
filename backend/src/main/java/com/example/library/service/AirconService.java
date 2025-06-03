package com.example.library.service;

import com.example.library.common.service.BaseService;
import com.example.library.pojo.entity.Aircon;
import java.util.List;

public interface AirconService extends BaseService<Aircon> {
    List<Aircon> getAllAircons();
    boolean increaseTemperature(String deviceName);
    boolean decreaseTemperature(String deviceName);
    boolean refrigerationmod(String deviceName);
    boolean heatingmod(String deviceName);
    boolean dehumidifiermod(String deviceName);
    boolean blowmod(String deviceName);
    boolean addAircon(Aircon aircon);

    boolean desenergy(String deviceName);
    boolean turnoffdesenergy(String deviceName);

    boolean wind1(String deviceName);
    boolean wind2(String deviceName);
    boolean wind3(String deviceName);
    boolean wind4(String deviceName);

    boolean wind5(String deviceName);


    boolean turnonlight(String deviceName);

    boolean turnofflight(String deviceName);
}