package com.example.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.library.pojo.entity.Light;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LightMapper extends BaseMapper<Light> {
    List<Light> selectAllLights();

    // 开灯
    @Update("""
        UPDATE light l
        LEFT JOIN device d ON l.device_id = d.device_id
        SET d.status = true,l.status = d.status
        WHERE d.device_name = #{deviceName}
        """)
    int turnOnLight(@Param("deviceName") String deviceName);

    // 关灯
    @Update("""
        UPDATE light l
        LEFT JOIN device d ON l.device_id = d.device_id
        SET d.status = false,l.status = d.status
        WHERE d.device_name = #{deviceName}
        """)
    int turnOffLight(@Param("deviceName") String deviceName);

    // 查询灯信息
    @Select("""
        SELECT l.*, d.device_name, d.status, d.category, d.detail, d.create_time, d.update_time
        FROM light l
        LEFT JOIN device d ON l.device_id = d.device_id
        WHERE d.device_name = #{deviceName}
        """)
    Light selectLightByDeviceName(@Param("deviceName") String deviceName);
}