package com.example.library.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.library.common.controller.BaseExtController;
import com.example.library.common.pojo.model.PageQuery;
import com.example.library.constant.AuthorizeConstant;
import com.example.library.mapper.DeviceMapper;
import com.example.library.pojo.dto.DeviceDTO;
import com.example.library.pojo.entity.Device;
import com.example.library.pojo.vo.DeviceSimpleVO;
import com.example.library.service.DeviceService;
import com.example.library.util.PageQueryUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/device")
@Tag(name = "设备信息接口", description = "设备信息接口")
// @PreAuthorize(AuthorizeConstant.HAS_ROLE_ADMIN)
public class DeviceController extends BaseExtController<Device, DeviceDTO, DeviceService> {
    @Resource
    private DeviceService deviceservice;
    @Resource
    // @Lazy
    DeviceMapper devicemapper;

    @GetMapping("/getall")
    @Operation(summary = "查看", description = "查看所有设备")
    public List<DeviceSimpleVO> getAllDevice() {
        List<Device> devices = deviceservice.getAllDevice();
        return devices.stream().map(device -> {
            DeviceSimpleVO vo = new DeviceSimpleVO();
            vo.setDeviceId(device.getDeviceId());
            vo.setDeviceName(device.getDeviceName());
            vo.setCategory(device.getCategory());
            vo.setStatus(device.getStatus());
            vo.setDetail(device.getDetail());
            return vo;
        }).collect(Collectors.toList());



    }

    @PostMapping("/deleteByDeviceName")
    @Operation(summary = "删除设备", description = "根据deviceName删除设备，同时删除aircon或light表中的内容")
    public Boolean deleteByDeviceName(@RequestParam String deviceName) {
        return deviceservice.deleteByDeviceName(deviceName);
    }


    //
    // @Override
    // @PostMapping("/save")
    // @Operation(summary = "新增", description = "传入新增对象")
    // public Boolean save(@Valid @RequestBody DeviceDTO dto) {
    //
    // Device device = devicePropertySet(dto);
    //
    // return service.save(device);
    // }
    //
    // @Override
    // @PostMapping("/update")
    // @Operation(summary = "修改", description = "传入修改对象")
    // public Boolean update(@Valid @RequestBody DeviceDTO dto) {
    // Device device = devicePropertySet(dto);
    // return service.updateById(device);
    // }
    //
    // @Override
    // @GetMapping("/detail")
    // @Operation(summary = "查看详情", description = "传入主键ID")
    // public Device detail(@Parameter(name = "主键", required = true) @RequestParam
    // Long id) {
    // return service.getById(id);
    // }
    //
    // @Override
    // @PostMapping("/page")
    // @Parameters({
    // @Parameter(name = "current", description = "当前页", in = ParameterIn.QUERY,
    // schema = @Schema(type = "int"), required = true),
    // @Parameter(name = "size", description = "每页的数量", in = ParameterIn.QUERY,
    // schema = @Schema(type = "int"), required = true)
    // })
    // @Operation(summary = "分页条件查询", description = "传入查询对象")
    // public IPage<Device> page(@RequestBody(required = false) Device entity,
    // @Parameter(hidden = true) PageQuery query) {
    // return service.page(PageQueryUtil.getPage(query), entity,
    // query.getQueryType());
    // }
    //
    // @Override
    // @PostMapping("/remove")
    // @Operation(summary = "删除", description = "传入ID主键")
    // public Boolean remove(@Parameter(name = "主键", required = true) @RequestParam
    // Long id) {
    //
    // return service.removeById(id);
    // }
    //
    //
    // @GetMapping("/borrow")
    // @Operation(summary = "查询借阅信息",description = "传入设备名")
    // public Device getBorrow(@Parameter(name="设备名",required=true) @RequestParam
    // String name ){
    // return devicemapper.selectDeviceByName(name);
    // }
    //
    // private Device devicePropertySet(DeviceDTO dto) {
    // Device device = new Device();
    // BeanUtil.copyProperties(dto, device);
    // return device;
    // }
}
