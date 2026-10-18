package org.example.restaurantmanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.CustomerLoginRequest;
import org.example.restaurantmanagement.dto.CustomerRegisterRequest;
import org.example.restaurantmanagement.dto.Result;
import org.example.restaurantmanagement.entity.User;
import org.example.restaurantmanagement.service.CustomerService;
import org.example.restaurantmanagement.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    /**
     * 顾客登录
     */
    @PostMapping("/login")
    public Result<?> login(
            @RequestBody
            @Valid
            CustomerLoginRequest request
    ) {

        User user = customerService.login(
                request.getPhone(),
                request.getPassword()
        );

        String token = JwtUtils.createToken(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

        Map<String, Object> data =
                new HashMap<>();

        data.put("token", token);
        data.put("user", user);

        return Result.success(data);
    }


    /**
     * 顾客注册
     */
    @PostMapping("/register")
    public Result<?> register(
            @RequestBody
            @Valid
            CustomerRegisterRequest request
    ) {

            User user = customerService.register(
                            request.getPhone(),
                            request.getPassword(),
                            request.getConfirmPassword());

            return Result.success(user);
    }
    
}