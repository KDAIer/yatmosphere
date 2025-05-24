package com.example.library.service;

import com.example.library.common.service.BaseService;
import com.example.library.pojo.entity.Device;

import java.util.List;

public interface DeviceService extends BaseService<Device> {
    List<Device> getAllDevice();
}