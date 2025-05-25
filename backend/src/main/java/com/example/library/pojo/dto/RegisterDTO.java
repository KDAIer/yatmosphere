package com.example.library.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "账户不能为空")
    private String account;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String name; // 可选字段

    @Schema(description = "邀请码（admin 自动生成，member 需填写）")
    private String inviteCode;

    @Schema(description = "用户类型：admin 或 member")
    @NotBlank(message = "用户类型不能为空")
    private String userType;
}