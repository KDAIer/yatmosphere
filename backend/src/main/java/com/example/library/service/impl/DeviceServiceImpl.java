package com.example.library.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.library.common.service.impl.BaseServiceImpl;
import com.example.library.etc.ServiceException;
import com.example.library.mapper.AirconMapper;
import com.example.library.mapper.DeviceMapper;
import com.example.library.mapper.LightMapper;
import com.example.library.mapper.RecordMapper;
import com.example.library.pojo.entity.*;
import com.example.library.service.DeviceService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
public class DeviceServiceImpl extends BaseServiceImpl<Device, DeviceMapper> implements DeviceService {


    @Resource
    @Lazy
    private AuthenticationManager authenticationManager;

    @Resource
    private DeviceMapper deviceMapper;

    @Lazy
    @Resource
    private DeviceService deviceService;
    @Resource
    private RecordMapper recordMapper;


    @Resource
    private AirconMapper airconMapper;
    @Resource
    private LightMapper lightMapper;

    @Transactional
    public boolean deleteByDeviceId(String deviceId) {
        // 查询设备信息
        Device device = deviceMapper.selectDeviceById(deviceId);
        if (device == null) {
            throw new ServiceException("设备不存在: " + deviceId);
        }

        int deleted = deviceMapper.deleteByDeviceId(deviceId);

        // 根据设备类型删除对应表中的记录
        if ("aircon".equalsIgnoreCase(device.getCategory())) {
            return airconMapper.deleteByDeviceId(deviceId)>0 && deleted>0;
        } else if ("light".equalsIgnoreCase(device.getCategory())) {
            return lightMapper.deleteByDeviceId(deviceId)>0 && deleted>0;
        }

        // 删除设备表中的记录

        return deleted > 0;
    }

    @Transactional(rollbackFor = Exception.class)
//    @Override
    public List<Device> getAllDevice() {
        return deviceMapper.selectAllDevices();
    }

    
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean save(Device entity) {
//        entity.setBorrowed(false);
//        if (!super.save(entity)) {
//            return false;
//        }
//        //persistHandle(entity, true,true);
//        return true;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean updateById(Device entity) {
//        Device device = super.getById(entity.getId());
//        if (device != null && device.getBorrowed() && entity.getBorrowed()) {
//            throw new ServiceException("设备借出中，无法修改");
//        }
//        if (!super.updateById(entity)) {
//            return false;
//        }
//
//        //persistHandle(entity, false,true);
//        return true;
//    }
//
//
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean removeById(Serializable id) {
//        Device device = super.getById(id);
//        if (device != null){System.out.println("device_name"+device.getDeviceName());}
//        if (device != null && device.getBorrowed()) {
//            throw new ServiceException("设备借出中，无法删除");
//        }
//        if (!super.removeById(id)) {
//            return false;
//        }
//
////        Device device = new Device();
////        device.setId((Long) id);
////        persistHandle(device, false,false);
//        return true;
//    }
////    @Override
////    public UserDetails loadDeviceByUsername(String boookname) throws UsernameNotFoundException {
////        Device device = baseMapper.selectDeviceByAccount(devicename);
////        if (device == null) {
////            throw new UsernameNotFoundException("用户名或密码错误");
////        }
////        return device;
////    }
////    public Device getBorrow(Long id){
////        Device device = baseMapper.selectDeviceByAccount(devicename);
////    }
//    public void persistHandle(Device entity, boolean isSave,boolean isUpdate) {
//
//        if (!isSave) {
//            LambdaUpdateWrapper<Device> wrapper = Wrappers.lambdaUpdate();
//            wrapper.eq(Device::getId, entity.getId());
//            if (!deviceService.remove(wrapper)) {
//                throw new ServiceException("清除用户旧数据发生异常");
//            }
//
//        }
//        if (isUpdate) {
//            if (!deviceService.save(entity)) {
//                throw new ServiceException("保存用户信息发生异常");
//            }
//        }
//
//    }
}
