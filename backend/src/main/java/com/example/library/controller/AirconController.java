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

    @PostMapping("/turnonlight")
    @Operation(summary = "打开空调灯光", description = "打开空调灯光")
    public Boolean turnonlight(@RequestParam String deviceName) {
        return airconService.turnonlight(deviceName);
    }

    @PostMapping("/turnofflight")
    @Operation(summary = "关闭空调灯光", description = "关闭空调灯光")
    public Boolean turnofflight(@RequestParam String deviceName) {
        return airconService.turnofflight(deviceName);
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
    @PostMapping("/refri")
    @Operation(summary = "制冷模式", description = "将deviceName对应空改为制冷模式")
    public Boolean refrigerationmod(@RequestParam String deviceName) {
        return airconService.refrigerationmod(deviceName);
    }
    @PostMapping("/heat")
    @Operation(summary = "制热模式", description = "将deviceName对应空改为制热模式")
    public Boolean heatingmod(@RequestParam String deviceName) {
        return airconService.heatingmod(deviceName);
    }
    @PostMapping("/dehumidifier")
    @Operation(summary = "抽湿模式", description = "将deviceName对应空改为抽湿模式")
    public Boolean dehumidifiermod(@RequestParam String deviceName) {
        return airconService.dehumidifiermod(deviceName);
    }
    @PostMapping("/blow")
    @Operation(summary = "送风模式", description = "将deviceName对应空改为送风模式")
    public Boolean blowmod(@RequestParam String deviceName) {
        return airconService.blowmod(deviceName);
    }
    @PostMapping("/desenergy")
    @Operation(summary = "打开空调节能模式", description = "打开空调节能模式")
    public Boolean desenergy(@RequestParam String deviceName) {
        return airconService.desenergy(deviceName);
    }
    @PostMapping("/turnoffdesenergy")
    @Operation(summary = "关闭空调节能模式", description = "关闭空调节能模式")
    public Boolean turnoffdesenergy(@RequestParam String deviceName) {
        return airconService.turnoffdesenergy(deviceName);
    }

    @PostMapping("/wind1")
    @Operation(summary = "调节风量等级为1", description = "调节风量等级为1")
    public Boolean wind1(@RequestParam String deviceName) {
        return airconService.wind1(deviceName);
    }

    @PostMapping("/wind2")
    @Operation(summary = "调节风量等级为2", description = "调节风量等级为2")
    public Boolean wind2(@RequestParam String deviceName) {
        return airconService.wind2(deviceName);
    }

    @PostMapping("/wind3")
    @Operation(summary = "调节风量等级为3", description = "调节风量等级为3")
    public Boolean wind3(@RequestParam String deviceName) {
        return airconService.wind3(deviceName);
    }

    @PostMapping("/wind4")
    @Operation(summary = "调节风量等级为4", description = "调节风量等级为4")
    public Boolean wind4(@RequestParam String deviceName) {
        return airconService.wind4(deviceName);
    }
    @PostMapping("/wind5")
    @Operation(summary = "调节风量等级为5", description = "调节风量等级为5")
    public Boolean wind5(@RequestParam String deviceName) {
        return airconService.wind5(deviceName);
    }
}