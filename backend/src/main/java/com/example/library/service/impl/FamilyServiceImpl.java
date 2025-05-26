package com.example.library.service.impl;

import com.example.library.pojo.dto.FamilyMemberDTO;
import com.example.library.pojo.entity.User;
import com.example.library.pojo.entity.UserEvent;
import com.example.library.service.FamilyService;
import com.example.library.service.UserEventService;
import com.example.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.hutool.core.lang.Console.print;

@Service
public class FamilyServiceImpl implements FamilyService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserEventService userEventService;

    @Override
    public List<FamilyMemberDTO> getFamilyMembersWithTodos(String currentAccount) {
        // 第一步：查出当前用户，拿到邀请码
        User currentUser = userService.getUserByAccount(currentAccount);
        print(currentUser);
        if (currentUser == null) {
            throw new RuntimeException("用户不存在");
        }

        String inviteCode = currentUser.getInviteCode();

        // 第二步：查出同一个邀请码的所有用户
        List<User> members = userService.getUsersByInviteCode(inviteCode);

        // 第三步：对每个用户查出他的事件，并封装成DTO
        List<FamilyMemberDTO> result = new ArrayList<>();
        for (User user : members) {
            List<UserEvent> events = userEventService.getEventsByAccount(user.getAccount());
            FamilyMemberDTO dto = new FamilyMemberDTO();
            dto.setName(user.getName());
            dto.setAdmin("admin".equals(user.getUserType()));
            dto.setAccount(user.getAccount());
            dto.setHome(true); // 暂时写死，等你有“在家”字段再处理
            dto.setTodos(events.stream().map(UserEvent::getEvent).collect(Collectors.toList()));
            result.add(dto);
        }

        return result;
    }
}
