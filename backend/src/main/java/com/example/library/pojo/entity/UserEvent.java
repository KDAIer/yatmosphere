package com.example.library.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_event")
public class UserEvent {

    private Long id;
    private String account;
    private String event;
}
