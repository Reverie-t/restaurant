package org.example.restaurantmanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.CustomerLoginRequest;
import org.example.restaurantmanagement.dto.CustomerLoginResponse;
import org.example.restaurantmanagement.dto.Result;
import org.example.restaurantmanagement.entity.User;
import org.example.restaurantmanagement.service.CustomerService;
import org.example.restaurantmanagement.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/login")
    public Result<?> login(
            @RequestBody @Valid CustomerLoginRequest request
    ) {

        User user =
                customerService.login(
                        request.getPhone(),
                        request.getPassword()
                );

        // 生成 JWT
        String token = JwtUtils.createToken(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

        // 返回 token + 用户信息
        return Result.success(
                new CustomerLoginResponse(
                        token,
                        user
                )
        );
    }
}