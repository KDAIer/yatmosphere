package com.example.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.library.pojo.entity.Device;
import org.apache.ibatis.annotations.Param;

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


}
