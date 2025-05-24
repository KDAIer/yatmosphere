package com.example.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.library.pojo.entity.Aircon;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AirconMapper extends BaseMapper<Aircon> {
    List<Aircon> selectAllAircons();

    // 升高温度
    @Update("UPDATE aircon a LEFT JOIN device d ON a.device_id = d.device_id SET a.temperature = a.temperature + 0.5 WHERE d.device_name = #{deviceName}")
    int increaseTemperatureByDeviceName(@Param("deviceName") String deviceName);

    // 降低温度
    @Update("UPDATE aircon a LEFT JOIN device d ON a.device_id = d.device_id SET a.temperature = a.temperature - 0.5 WHERE d.device_name = #{deviceName}")
    int decreaseTemperatureByDeviceName(@Param("deviceName") String deviceName);
}