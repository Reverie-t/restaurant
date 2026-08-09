package org.example.restaurantmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserAddRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3~20之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6~20之间")
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String realName;

    @Pattern(
            regexp = "^1[3-9]\\d{9}$",
            message = "手机号格式错误"
    )
    private String phone;

    @Email(message = "邮箱格式错误")
    private String email;

    @NotBlank(message = "角色不能为空")
    private String role;
}