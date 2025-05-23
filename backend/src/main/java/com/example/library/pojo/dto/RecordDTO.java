package com.example.library.pojo.dto;

import com.example.library.common.pojo.dto.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Date;

/**
 * @author: zyh
 * 
 * @version: v1.0.0
 * 
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class RecordDTO extends BaseDTO {
    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank(message = "设备名不能为空")
    private String deviceName;

    private Date borrowTime;
    private Date returnTime;
}
