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



    boolean setWindLevel(String deviceName, int level);
    boolean updateAirconPower(String deviceId, int status);
    boolean updateAirconMode(String deviceId, String mode);

    boolean turnonlight(String deviceName);

    boolean turnofflight(String deviceName);
}