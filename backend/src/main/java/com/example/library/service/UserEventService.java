package com.example.library.service;

import com.example.library.pojo.entity.UserEvent;

import java.util.List;

public interface UserEventService {
    List<UserEvent> getEventsByAccount(String account);
}
