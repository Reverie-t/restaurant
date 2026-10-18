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


    /**
     * 顾客登录
     */
    public User login(
            String phone,
            String password
    ) {

        User user =
                userMapper.selectOne(
                        new LambdaQueryWrapper<User>()
                                .eq(
                                        User::getPhone,
                                        phone
                                )
                                .eq(
                                        User::getRole,
                                        "user"
                                )
                                .last("LIMIT 1")
                );

        if (user == null) {

            throw new RuntimeException(
                    "该手机号未注册，请先注册"
            );

        }

        if (
                !user.getPassword()
                        .equals(password)
        ) {

            throw new RuntimeException(
                    "手机号或密码错误"
            );

        }

        return user;
    }


    /**
     * 顾客注册
     */
    public User register(
            String phone,
            String password,
            String confirmPassword
    ) {

        if (
                password == null ||
                        confirmPassword == null ||
                        !password.equals(confirmPassword)
        ) {

            throw new RuntimeException(
                    "两次输入的密码不一致"
            );

        }


        User existUser =
                userMapper.selectOne(
                        new LambdaQueryWrapper<User>()
                                .eq(
                                        User::getPhone,
                                        phone
                                )
                                .eq(
                                        User::getRole,
                                        "user"
                                )
                                .last("LIMIT 1")
                );


        if (existUser != null) {

            throw new RuntimeException(
                    "该手机号已经注册"
            );

        }


        User customer =
                new User();

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
}