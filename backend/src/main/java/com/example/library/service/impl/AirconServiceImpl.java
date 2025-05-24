package com.example.library.service.impl;

import com.example.library.common.service.impl.BaseServiceImpl;
import com.example.library.mapper.AirconMapper;
import com.example.library.pojo.entity.Aircon;
import com.example.library.service.AirconService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AirconServiceImpl extends BaseServiceImpl<Aircon, AirconMapper> implements AirconService {
    @Resource
    private AirconMapper airconMapper;

    @Override
    public List<Aircon> getAllAircons() {
        return airconMapper.selectAllAircons();
    }

    @Override
    @Transactional
    public boolean increaseTemperature(String deviceName) {
        int updated = airconMapper.increaseTemperatureByDeviceName(deviceName);
        // 查询当前温度和原detail
        Aircon aircon = airconMapper.selectTempAndModeByDeviceName(deviceName);
        String oldDetail = aircon.getDetail();
        double newTemp = aircon.getTemperature();
        String newDetail = updateDetailTemperature(oldDetail, newTemp);
        airconMapper.updateDeviceDetail(deviceName, newDetail);
        return updated > 0;
    }

    @Override
    @Transactional
    public boolean decreaseTemperature(String deviceName) {
        // 根据设备名称减少温度
        int updated = airconMapper.decreaseTemperatureByDeviceName(deviceName);
        // 根据设备名称查询温度和模式
        Aircon aircon = airconMapper.selectTempAndModeByDeviceName(deviceName);
        // 如果查询结果不为空
        if (aircon != null) {
            // 格式化温度和模式
            String detail = formatDetail(aircon.getTemperature(), aircon.getMode());
            // 更新设备详情
            airconMapper.updateDeviceDetail(deviceName, detail);
        }
        // 返回更新结果
        return updated > 0;
    }

    private String formatDetail(Double temperature, String mode) {
        // 只保留整数部分
        DecimalFormat df = new DecimalFormat("#.0");
        return df.format(temperature) + "℃ " + mode + "模式";
    }

    private String updateDetailTemperature(String detail, double newTemperature) {
        // 匹配以数字开头，后跟“℃”
        Pattern pattern = Pattern.compile("^[\\d.]+(?=℃)");
        Matcher matcher = pattern.matcher(detail);
        if (matcher.find()) {
            // 替换为新温度（保留1位小数）
            String newTempStr = String.format("%.1f", newTemperature);
            return matcher.replaceFirst(newTempStr);
        }
        // 如果没有匹配，直接返回原字符串
        return detail;
    }
}