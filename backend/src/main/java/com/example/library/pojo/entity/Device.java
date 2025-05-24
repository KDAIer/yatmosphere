package com.example.library.pojo.entity;

import com.example.library.annotations.UniqCheck;
import com.example.library.annotations.UniqColumn;
import com.example.library.common.pojo.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author zyh
 * @create 2024/7/30
 */

@Data
@EqualsAndHashCode(callSuper = true)
@UniqCheck
public class Device extends BaseEntity {
//    @UniqColumn(message = "已存在重复设备")
//    private Integer id;

    private String deviceName;

    @UniqColumn(message = "已存在重复设备")
    private String deviceId; // 对应数据库字段 device_id，不是 isbn

    private String category;

    private Boolean status; // 0=开启，1=关闭

    private String detail;

//    private java.time.LocalDateTime createTime;
//
//    private java.time.LocalDateTime updateTime;
}