package org.example.restaurantmanagement.interceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.restaurantmanagement.utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        String token = request.getHeader("Authorization");

        if (token == null || token.isBlank()) {

            response.setStatus(401);
            response.getWriter().write("Token不存在");

            return false;
        }

        // 支持 Bearer Token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!JwtUtils.verify(token)) {

            response.setStatus(401);
            response.getWriter().write("Token无效");

            return false;
        }

        // 解析 Token，方便后续使用
        Claims claims = JwtUtils.parseToken(token);

        request.setAttribute("userId", claims.get("id"));
        request.setAttribute("username", claims.get("username"));
        request.setAttribute("role", claims.get("role"));

        return true;
    }
}