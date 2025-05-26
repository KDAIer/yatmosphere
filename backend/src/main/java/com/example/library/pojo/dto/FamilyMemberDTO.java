package com.example.library.pojo.dto;

import lombok.Data;
import java.util.List;

@Data
public class FamilyMemberDTO {
    private String name;
    private boolean isAdmin;
    private boolean isHome;
    private String account;
    private List<String> todos;
}
