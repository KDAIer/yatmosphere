package com.example.library.controller;

import com.example.library.pojo.entity.Aircon;
import com.example.library.service.AirconService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aircon")
@Tag(name = "空调信息接口", description = "空调信息接口")
// @PreAuthorize("hasRole('ADMIN')")
public class AirconController {
    @Resource
    private AirconService airconService;


    @PostMapping("/add")
    @Operation(summary = "新增空调", description = "新增空调（设备+空调表）")
    public Boolean addAircon(@RequestBody Aircon aircon) {
        return airconService.addAircon(aircon);
    }

    @GetMapping("/getall")
    @Operation(summary = "查看所有空调", description = "查看所有空调")
    public List<Aircon> getAllAircons() {
        return airconService.getAllAircons();
    }

     @PostMapping("/inc")
     @Operation(summary = "升高温度", description = "根据deviceName升高温度0.5度")
     public Boolean increaseTemperature(@RequestParam String deviceName) {
         return airconService.increaseTemperature(deviceName);
     }

     @PostMapping("/dec")
     @Operation(summary = "降低温度", description = "根据deviceName降低温度0.5度")
     public Boolean decreaseTemperature(@RequestParam String deviceName) {
         return airconService.decreaseTemperature(deviceName);
     }
}