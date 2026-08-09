package org.example.restaurantmanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.LoginRequest;
import org.example.restaurantmanagement.dto.Result;
import org.example.restaurantmanagement.dto.UserAddRequest;
import org.example.restaurantmanagement.dto.UserUpdateRequest;
import org.example.restaurantmanagement.entity.User;
import org.example.restaurantmanagement.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public Result<?> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username) {

        return Result.success(
                userService.list(pageNum, pageSize, username)
        );
    }
    @PutMapping("/user")
    public Result<?> updateUser(@Valid @RequestBody UserUpdateRequest request){

        userService.update(request);

        return Result.success(null);
    }

    @PostMapping("/user")
    public Result<?> addUser(@Valid @RequestBody UserAddRequest request){

        userService.add(request);

        return Result.success(null);
    }
    @DeleteMapping("/user/{id}")
    public Result<?> deleteUser(@PathVariable Long id){

        userService.delete(id);

        return Result.success(null);
    }


}