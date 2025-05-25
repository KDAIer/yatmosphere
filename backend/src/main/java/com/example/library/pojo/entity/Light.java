package com.example.library.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("light")
public class Light extends Device {
    @TableField("brightness")
    private Integer brightness;

    @TableField("color_temp")
    private String colorTemp;
}