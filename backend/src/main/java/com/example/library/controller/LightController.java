package com.example.library.controller;

import com.example.library.pojo.entity.Light;
import com.example.library.pojo.vo.Result;
import com.example.library.service.LightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.library.etc.ResultGenerator.genFail;

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


    @PostMapping("/add")
    @Operation(summary = "新增灯", description = "新增灯（设备+灯表）")
    public Result<Boolean> addLight(@RequestBody Light light) {
        try {
            boolean result = lightService.addLight(light);
            if (result) {
                return Result.success(true);
            } else {
                return Result.fail("添加失败");
            }
        } catch(
        RuntimeException e)
        {
            return Result.fail(e.getMessage());
        }
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

    @PostMapping("/brightness")
    @Operation(summary = "调节灯的亮度", description = "调节灯的亮度")
    public Boolean brightness(@RequestParam String deviceName, @RequestParam int brightness) {
        return lightService.brightness(deviceName,brightness);
    }

    @PostMapping("/naturelight")
    @Operation(summary = "调节灯为自然模式", description = "调节灯为自然模式")
    public Boolean naturelight(@RequestParam String deviceName) {
        return lightService.naturelight(deviceName);
    }

    @PostMapping("/warmlight")
    @Operation(summary = "调节灯为暖光模式", description = "调节灯为暖光模式")
    public Boolean warmlight(@RequestParam String deviceName) {
        return lightService.warmlight(deviceName);
    }

}