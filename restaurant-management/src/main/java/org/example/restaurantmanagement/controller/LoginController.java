package org.example.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.LoginRequest;
import org.example.restaurantmanagement.dto.Result;
import org.example.restaurantmanagement.entity.User;
import org.example.restaurantmanagement.service.UserService;
import org.example.restaurantmanagement.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class LoginController {

    private final UserService userService;


    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginRequest request) {

        User user = userService.login(
                request.getUsername(),
                request.getPassword());

        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        String token = JwtUtils.createToken(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

        Map<String, Object> map = new HashMap<>();

        map.put("token", token);
        map.put("user", user);

        return Result.success(map);
    }
}