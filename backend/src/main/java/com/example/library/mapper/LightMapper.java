package com.example.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.library.pojo.entity.Light;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface LightMapper extends BaseMapper<Light> {
    List<Light> selectAllLights();

    @Insert("""
        INSERT INTO light (device_id, brightness, color_temp)
        VALUES (#{deviceId}, #{brightness}, #{colorTemp})
        """)
    int insertLight(Light light);


    @Delete("DELETE FROM light WHERE device_id = #{deviceId}")
    int deleteByDeviceId(@Param("deviceId") String deviceId);


    // 开灯
    @Update("""
        UPDATE light l
        LEFT JOIN device d ON l.device_id = d.device_id
        SET d.status = true,l.status = true
        WHERE d.device_name = #{deviceName}
        """)
    int turnOnLight(@Param("deviceName") String deviceName);

    // 关灯
    @Update("""
        UPDATE light l
        LEFT JOIN device d ON l.device_id = d.device_id
        SET d.status = false,l.status = false
        WHERE d.device_name = #{deviceName}
        """)
    int turnOffLight(@Param("deviceName") String deviceName);

    @Update("""
        UPDATE light l
        LEFT JOIN device d ON l.device_id = d.device_id
        SET l.brightness = #{brightness}
        WHERE d.device_name = #{deviceName}
        """)
    int brightness(@Param("deviceName") String deviceName, @Param("brightness") int brightness);
    @Update("""
        UPDATE light l
        LEFT JOIN device d ON l.device_id = d.device_id
        SET l.color_temp = "自然"
        WHERE d.device_name = #{deviceName}
        """)
    int naturelight(@Param("deviceName") String deviceName);
    @Update("""
        UPDATE light l
        LEFT JOIN device d ON l.device_id = d.device_id
        SET l.color_temp = "暖光"
        WHERE d.device_name = #{deviceName}
        """)
    int warmlight(@Param("deviceName") String deviceName);

    // 查询灯信息
    @Select("""
        SELECT l.*, d.device_name, d.status, d.category, d.detail, d.create_time, d.update_time
        FROM light l
        LEFT JOIN device d ON l.device_id = d.device_id
        WHERE d.device_name = #{deviceName}
        """)
    Light selectLightByDeviceName(@Param("deviceName") String deviceName);
}