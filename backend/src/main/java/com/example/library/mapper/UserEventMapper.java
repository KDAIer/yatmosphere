package com.example.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.library.pojo.entity.UserEvent;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserEventMapper extends BaseMapper<UserEvent> {

    /**
     * 根据账号查询用户所有事件
     */
    @Select("SELECT * FROM user_event WHERE account = #{account}")
    List<UserEvent> selectEventsByAccount(@Param("account") String account);


}
