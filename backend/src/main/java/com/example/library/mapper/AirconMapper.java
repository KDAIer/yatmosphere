package com.example.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.library.pojo.entity.Aircon;
import java.util.List;

public interface AirconMapper extends BaseMapper<Aircon> {
    List<Aircon> selectAllAircons();
}