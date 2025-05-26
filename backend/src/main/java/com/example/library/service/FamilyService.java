package com.example.library.service;

import com.example.library.pojo.dto.FamilyMemberDTO;

import java.util.List;

public interface FamilyService {
    List<FamilyMemberDTO> getFamilyMembersWithTodos(String currentAccount);
}
