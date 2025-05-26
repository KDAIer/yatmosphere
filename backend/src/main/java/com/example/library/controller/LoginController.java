package com.example.library.controller;

import com.example.library.pojo.vo.Result;
import com.example.library.etc.ServiceException;
import com.example.library.pojo.dto.RegisterDTO;
import com.example.library.pojo.dto.LoginDTO;
import com.example.library.pojo.vo.LoginVO;
import com.example.library.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/auth")
@Tag(name = "登陆接口", description = "登陆接口")
public class LoginController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    public LoginVO login(@Valid @RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    @PostMapping("/register")
    public Result<Boolean> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            boolean success = userService.register(registerDTO);
            if (success) {
                return Result.success(true); // data里放Boolean
            } else {
                return Result.fail("注册失败");
            }
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/")
    public String health() {
        return "OK";
    }
}
