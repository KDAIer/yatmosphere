package com.example.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.library.mapper.UserEventMapper;
import com.example.library.pojo.entity.UserEvent;
import com.example.library.service.UserEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserEventServiceImpl implements UserEventService {

    @Autowired
    private UserEventMapper userEventMapper;

    @Override
    public List<UserEvent> getEventsByAccount(String account) {
        return userEventMapper.selectEventsByAccount(account);
        // 或者：
        // return userEventMapper.selectList(new QueryWrapper<UserEvent>().eq("account", account));
    }
}
