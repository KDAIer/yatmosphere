package com.example.library.pojo.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceSimpleVO {
    private String deviceId;
    private String deviceName;
    private String category;
    private Boolean status;
    private String detail;
    // 不包含 createTime

}