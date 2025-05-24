package com.example.library.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.library.annotations.UniqCheck;
import com.example.library.common.pojo.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@UniqCheck
@TableName("aircon")
public class Aircon extends Device {

    @TableField("temperature")
    private Double temperature; // 改为Double
    @TableField("mode")
    private String mode;
    @TableField("fan_level")
    private Integer fanLevel;
    @TableField("timer")
    private Integer timer; // 定时分钟数或小时数
}
