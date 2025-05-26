package com.example.library.authorize;

import com.example.library.etc.ResultGenerator;
import com.example.library.pojo.entity.User;
import com.example.library.pojo.model.UserJwtInfo;
import com.example.library.util.JwtTokenUtil;
import com.example.library.util.ResponseUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WangYi
 * @create 2024/7/30
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final List<String> WHITE_LIST = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/api/family-members"
    );

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    @Lazy
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // ✅ 白名单路径直接放行
        if (WHITE_LIST.contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 检查 Authorization 头
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(jwtToken)) {
            filterChain.doFilter(request, response); // ⚠️ 也可直接拒绝返回 401，看需求
            return;
        }

        // ✅ 从 token 中解析用户信息
        UserJwtInfo userJwtInfo = jwtTokenUtil.getUserJwtInfo(jwtToken);
        UserDetails targetUser = userDetailsService.loadUserByUsername(userJwtInfo.getAccount());
        if (targetUser == null) {
            ResponseUtil.response(response, ResultGenerator.genFail(HttpStatus.UNAUTHORIZED));
            return;
        }

        // ✅ 将认证信息写入上下文
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                targetUser, null, targetUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}

