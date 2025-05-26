package com.example.library.controller;

import com.example.library.pojo.dto.FamilyMemberDTO;
import com.example.library.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FamilyController {

    @Autowired
    private FamilyService familyService; // ✅ 注入接口的实现类实例

    @GetMapping("/family-members")
    public List<FamilyMemberDTO> getFamilyMembers(@RequestParam String account) {
        return familyService.getFamilyMembersWithTodos(account); // ✅ 正确调用方式
    }
}
