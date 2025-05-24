package com.example.library.service.impl;

import com.example.library.common.service.impl.BaseServiceImpl;
import com.example.library.mapper.AirconMapper;
import com.example.library.pojo.entity.Aircon;
import com.example.library.service.AirconService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AirconServiceImpl extends BaseServiceImpl<Aircon, AirconMapper> implements AirconService {
    @Resource
    private AirconMapper airconMapper;

    @Override
    public List<Aircon> getAllAircons() {
        return airconMapper.selectAllAircons();
    }
}