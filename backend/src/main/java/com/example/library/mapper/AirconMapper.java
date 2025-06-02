package com.example.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.library.pojo.entity.Aircon;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AirconMapper extends BaseMapper<Aircon> {
    List<Aircon> selectAllAircons();

    @Insert("""
    INSERT INTO aircon (device_id, temperature, mode, fan_level, timer)
    VALUES (#{deviceId}, #{temperature}, #{mode}, #{fanLevel}, #{timer})
    """)
    int insertAircon(Aircon aircon);


    

    @Update("""
        UPDATE aircon
        SET status = #{status}
        WHERE device_id = #{deviceId}
        """)
    int updatePowerByDeviceId(@Param("deviceId") String deviceId, @Param("status") int status);

    // 升高温度
    @Update("""
        UPDATE aircon a
        LEFT JOIN device d ON a.device_id = d.device_id
        SET a.temperature = a.temperature + 0.5
        WHERE d.device_name = #{deviceName}
        """)
    int increaseTemperatureByDeviceName(@Param("deviceName") String deviceName);

    // 降低温度
    @Update("""
        UPDATE aircon a
        LEFT JOIN device d ON a.device_id = d.device_id
        SET a.temperature = a.temperature - 0.5
        WHERE d.device_name = #{deviceName}
        """)
    int decreaseTemperatureByDeviceName(@Param("deviceName") String deviceName);

    @Select("""
    SELECT a.temperature, a.mode, d.detail 
    FROM aircon a
    LEFT JOIN device d ON a.device_id = d.device_id 
    WHERE a.device_id = #{deviceId}
    """)
    Aircon selectTempAndModeByDeviceId(@Param("deviceId") String deviceId);
    
    @Update("""
        UPDATE aircon a
        LEFT JOIN device d ON a.device_id = d.device_id
        SET a.mode = #{mode}, d.detail = #{detail}
        WHERE a.device_id = #{deviceId}
        """)
    int updateModeByDeviceId(@Param("deviceId") String deviceId,
                             @Param("mode") String mode,
                             @Param("detail") String detail);
    @Update("UPDATE aircon a LEFT JOIN device d ON a.device_id = d.device_id SET a.mode = '制冷' WHERE d.device_name = #{deviceName}")
    int refrigerationmod(@Param("deviceName") String deviceName);

    @Update("UPDATE aircon a LEFT JOIN device d ON a.device_id = d.device_id SET a.mode = '制热' WHERE d.device_name = #{deviceName}")
    int heatingmod(@Param("deviceName") String deviceName);

    @Update("UPDATE aircon a LEFT JOIN device d ON a.device_id = d.device_id SET a.mode = '除湿' WHERE d.device_name = #{deviceName}")
    int dehumidifiermod(@Param("deviceName") String deviceName);


    @Update("UPDATE aircon a LEFT JOIN device d ON a.device_id = d.device_id SET a.mode = '送风' WHERE d.device_name = #{deviceName}")
    int blowmod(@Param("deviceName") String deviceName);

    // 查询当前温度、模式和detail
    @Select("""
        SELECT a.temperature, a.mode, d.detail
        FROM aircon a
        LEFT JOIN device d ON a.device_id = d.device_id
        WHERE d.device_name = #{deviceName}
        """)
    Aircon selectTempAndModeByDeviceName(@Param("deviceName") String deviceName);

    // 更新设备描述
    @Update("""
        UPDATE device d
        LEFT JOIN aircon a ON a.device_id = d.device_id
        SET d.detail = #{detail}
        WHERE d.device_name = #{deviceName}
        """)
    int updateDeviceDetail(@Param("deviceName") String deviceName, @Param("detail") String detail);
}