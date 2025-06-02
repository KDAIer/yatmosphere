package com.example.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.library.pojo.entity.Device;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author zyh
 * @create 2021/7/31
 */
public interface DeviceMapper extends BaseMapper<Device> {
    /**
     * 按账号名查询用户信息
     * @param name
     * @return
     */
    Device selectDeviceByName(@Param("name") String name);
    @Select("SELECT * FROM device WHERE device_id = #{deviceId}")
    Device selectById(String deviceId);


    @Insert("""
    INSERT INTO device (device_id, device_name, category, status, detail)
    VALUES (#{deviceId}, #{deviceName}, #{category}, #{status}, #{detail})
    """)
    int insertDevice(Device device);


    @Update("""
    UPDATE device 
    SET status = #{status}
    WHERE device_id = #{deviceId}
    """)
    int updateStatusByDeviceId(@Param("deviceId") String deviceId, @Param("status") int status);

//    @Select("SELECT * FROM device")
    List<Device> selectAllDevices();

}
