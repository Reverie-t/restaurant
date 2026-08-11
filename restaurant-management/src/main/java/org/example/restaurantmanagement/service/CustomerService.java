package org.example.restaurantmanagement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.entity.User;
import org.example.restaurantmanagement.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserMapper userMapper;

    public User login(String phone, String password) {

        // 查询顾客
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getPhone, phone)
                        .eq(User::getRole, "user")
                        .last("LIMIT 1")
        );

        // =========================
        // 用户不存在 → 自动注册
        // =========================

        if (user == null) {

            User customer = new User();

            customer.setUsername(phone);

            customer.setPassword(password);

            customer.setRealName("顾客");

            customer.setPhone(phone);

            customer.setRole("user");

            customer.setCreateTime(
                    LocalDateTime.now()
            );

            userMapper.insert(customer);

            return customer;
        }

        // =========================
        // 用户存在 → 验证密码
        // =========================

        if (!user.getPassword().equals(password)) {

            throw new RuntimeException(
                    "手机号或密码错误"
            );
        }

        return user;
    }
}