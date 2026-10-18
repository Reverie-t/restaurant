package org.example.restaurantmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CustomerRegisterRequest {

    /**
     * 手机号
     */
    @NotBlank(message = "请输入手机号")
    @Pattern(
            regexp = "^1[3-9]\\d{9}$",
            message = "手机号格式不正确"
    )
    private String phone;

    /**
     * 密码
     */
    @NotBlank(message = "请输入密码")
    private String password;

    /**
     * 确认密码
     */
    @NotBlank(message = "请再次输入密码")
    private String confirmPassword;
}