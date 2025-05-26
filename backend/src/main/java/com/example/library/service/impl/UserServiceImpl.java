package com.example.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.library.common.service.impl.BaseServiceImpl;
import com.example.library.constant.CommonConstant;
import com.example.library.etc.ServiceException;
import com.example.library.mapper.UserMapper;
import com.example.library.pojo.dto.LoginDTO;
import com.example.library.pojo.entity.User;
import com.example.library.pojo.entity.UserRole;
import com.example.library.pojo.vo.LoginVO;
import com.example.library.service.RoleService;
import com.example.library.service.UserRoleService;
import com.example.library.service.UserService;
import com.example.library.util.JwtTokenUtil;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.example.library.pojo.dto.RegisterDTO;

import javax.security.auth.login.AccountExpiredException;

import static cn.hutool.core.lang.Console.print;

/**
 * @author WangYi
 * @create 2024/7/30
 */

@Service
public class UserServiceImpl extends BaseServiceImpl<User, UserMapper> implements UserService, UserDetailsService {
    @Resource
    private RoleService roleService;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    @Lazy
    private AuthenticationManager authenticationManager;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(User entity) {
        if (!StringUtils.hasText(entity.getName())) {
            entity.setName(entity.getAccount());
        }
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        if (!super.save(entity)) {
            return false;
        }
        persistHandle(entity, true);
        return true;
//<<<<<<< HEAD
//=======
//
//>>>>>>> 2a9e10a63fd57a2146a6822c7d4cac315e689fac
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(User entity) {
        if (!StringUtils.hasText(entity.getName())) {
            entity.setName(entity.getAccount());
        }
        if (StringUtils.hasText(entity.getPassword())) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        }
        if (!super.updateById(entity)) {
            return false;
        }
        persistHandle(entity, false);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = baseMapper.selectUserByAccount(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        System.out.println("[Debug] loadUserByUsername 查出的密码：" + user.getPassword());
        return user;
    }

    public void persistHandle(User entity, boolean isSave) {
        List<UserRole> userRoleList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(entity.getRoleList())) {
            userRoleList = entity.getRoleList().stream().map(item -> {
                UserRole userRole = new UserRole();
                userRole.setUserId(entity.getId());
                userRole.setRoleId(item.getId());
                return userRole;
            }).collect(Collectors.toList());
        } else {
            UserRole userRole = new UserRole();
            userRole.setUserId(entity.getId());
            userRole.setRoleId(roleService.getByCode(CommonConstant.ROLE_USER).getId());
            userRoleList.add(userRole);
        }
        if (!isSave) {
            LambdaUpdateWrapper<UserRole> wrapper = Wrappers.lambdaUpdate();
            wrapper.eq(UserRole::getUserId, entity.getId());
            if (!userRoleService.remove(wrapper)) {
                throw new ServiceException("清除用户旧数据发生异常");
            }
        }
        if (!userRoleService.saveBatch(userRoleList)) {
            throw new ServiceException("保存用户信息发生异常");
        }
    }

    /**
     * 调试用：测试明文密码和数据库加密密码是否匹配，并打印结果
     * 
     * @param account     用户账号
     * @param rawPassword 用户输入的明文密码
     */
    public void debugPasswordMatch(String account, String rawPassword) {
        User user = baseMapper.selectUserByAccount(account);
        if (user == null) {
            System.out.println("[Debug] 用户不存在: " + account);
            return;
        }

        String encodedPassword = user.getPassword();
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        String encoded = passwordEncoder.encode(rawPassword);
        System.out.println("[Debug] 账号: " + account);
        System.out.println("[Debug] 输入密码: " + rawPassword);
        System.out.println("[Debug] 明文密码加密后结果: " + encoded);
        System.out.println("[Debug] 数据库加密密码: " + encodedPassword);
        System.out.println("[Debug] 密码匹配结果: " + matches);
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 调试打印密码是否匹配，方便排查
        debugPasswordMatch(loginDTO.getAccount(), loginDTO.getPassword());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginDTO.getAccount(),
                loginDTO.getPassword());
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(token);
        } catch (LockedException e) {
            throw new ServiceException("账号已被锁定，请联系管理员");
        } catch (DisabledException e) {
            throw new ServiceException("账号被禁用");
        } catch (CredentialsExpiredException e) {
            throw new ServiceException("密码已过期");
        } catch (BadCredentialsException e) {
            throw new ServiceException("用户名或密码错误");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("登录失败，请稍后再试");
        }
        if (authentication == null) {
            throw new ServiceException("用户名或密码错误");
        }

        User targetUser = (User) authentication.getPrincipal();
        LoginVO loginVO = new LoginVO();
        loginVO.setId(targetUser.getId());
        loginVO.setAccount(targetUser.getAccount());
        loginVO.setToken(jwtTokenUtil.generateUserToken(targetUser));
        loginVO.setRole(targetUser.getUserType());
        loginVO.setInviteCode(targetUser.getInviteCode());
        return loginVO;
//<<<<<<< HEAD
//
//=======
//>>>>>>> 2a9e10a63fd57a2146a6822c7d4cac315e689fac
    }

    @Override
    @Transactional
    public Boolean register(RegisterDTO dto) {
        // 1. 校验账号是否重复
        if (lambdaQuery().eq(User::getAccount, dto.getAccount()).count() > 0) {
            throw new ServiceException("账号已存在");
        }

        // 2. 构建用户对象
        User user = new User();
        user.setAccount(dto.getAccount());
        user.setPassword(dto.getPassword());
        user.setName(dto.getName() != null ? dto.getName() : dto.getAccount());
        user.setInviteCode(dto.getInviteCode());
        user.setUserType(dto.getUserType());

        // 3. 根据 userType 判断逻辑
        if ("member".equalsIgnoreCase(dto.getUserType())) {
            // 校验邀请码存在性
            long count = lambdaQuery()
                    .eq(User::getInviteCode, dto.getInviteCode())
                    .eq(User::getUserType, "admin")
                    .count();
            if (count == 0) {
                throw new ServiceException("无效的邀请码");
            }
        } else if (!"admin".equalsIgnoreCase(dto.getUserType())) {
            throw new ServiceException("不支持的用户类型");
        }

        // 4. 保存用户
        return this.save(user);
    }

}
