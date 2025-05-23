package com.example.library.pojo.dto;

import com.example.library.common.pojo.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zyh
 * @create 2024/7/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceDTO extends BaseDTO {
    @Schema(description = "设备名")
    @NotBlank(message = "设备名不能为空")
    String device_name;
    @Schema(description = "ISBN")
    @NotBlank(message = "ISBN号不能为空")
    String isbn;
    @Schema(description = "类别")

    String category;
    @Schema(description = "是否被借")
    Boolean borrowed;

}