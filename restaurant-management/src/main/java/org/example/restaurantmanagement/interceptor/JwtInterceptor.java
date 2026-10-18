package org.example.restaurantmanagement.interceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.restaurantmanagement.utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        // OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            return true;
        }


        // =========================
        // 公开 GET 接口
        // =========================

        String uri = request.getRequestURI();

        if ("GET".equalsIgnoreCase(request.getMethod())) {

            if (
                    "/api/dish".equals(uri)
                            || "/api/dish/menu".equals(uri)
                            || "/api/categories".equals(uri)
                            || "/api/category/simple".equals(uri)
                            || "/api/table".equals(uri)
            ) {

                return true;
            }
        }


        // =========================
        // 获取 Token
        // =========================

        String token =
                request.getHeader("Authorization");


        if (token == null || token.isBlank()) {

            response.setStatus(401);

            response.getWriter().write(
                    "Token不存在"
            );

            return false;
        }


        // =========================
        // Bearer Token
        // =========================

        if (token.startsWith("Bearer ")) {

            token = token.substring(7);
        }


        // =========================
        // 验证 Token
        // =========================

        if (!JwtUtils.verify(token)) {

            response.setStatus(401);

            response.getWriter().write(
                    "Token无效"
            );

            return false;
        }


        // =========================
        // 解析 Token
        // =========================

        Claims claims =
                JwtUtils.parseToken(token);


        request.setAttribute(
                "userId",
                claims.get("id")
        );

        request.setAttribute(
                "username",
                claims.get("username")
        );

        request.setAttribute(
                "role",
                claims.get("role")
        );


        return true;
    }
}