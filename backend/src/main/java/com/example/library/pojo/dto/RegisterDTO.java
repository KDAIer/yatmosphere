package com.example.library.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "账户不能为空")
    private String account;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    private String name; // 可选字段
}