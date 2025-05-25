package com.example.library.controller;

import com.example.library.pojo.entity.Light;
import com.example.library.service.LightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/light")
@Tag(name = "灯信息接口", description = "灯信息接口")
public class LightController {
    @Resource
    private LightService lightService;

    @GetMapping("/getall")
    @Operation(summary = "查看所有灯", description = "查看所有灯")
    public List<Light> getAllLights() {
        return lightService.getAllLights();
    }

    @PostMapping("/on")
    @Operation(summary = "开灯", description = "根据deviceName开灯")
    public Boolean turnOnLight(@RequestParam String deviceName) {
        return lightService.turnOnLight(deviceName);
    }

    @PostMapping("/off")
    @Operation(summary = "关灯", description = "根据deviceName关灯")
    public Boolean turnOffLight(@RequestParam String deviceName) {
        return lightService.turnOffLight(deviceName);
    }
}