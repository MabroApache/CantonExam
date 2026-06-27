package com.cantonfair.exam.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.cantonfair.exam.common.Result;
import com.cantonfair.exam.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果是OPTIONS请求，直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            return unauthorized(response, "请先登录");
        }

        if (!jwtUtil.verifyToken(token)) {
            return unauthorized(response, "Token验证失败");
        }

        if (jwtUtil.isTokenExpired(token)) {
            return unauthorized(response, "Token已过期，请重新登录");
        }

        // 将用户信息存入request
        request.setAttribute("userId", jwtUtil.getUserId(token));
        request.setAttribute("username", jwtUtil.getUsername(token));
        request.setAttribute("role", jwtUtil.getRole(token));
        request.setAttribute("departmentId", jwtUtil.getDepartmentId(token));
        
        return true;
    }

    private boolean unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(Result.error(401, message)));
        return false;
    }
}