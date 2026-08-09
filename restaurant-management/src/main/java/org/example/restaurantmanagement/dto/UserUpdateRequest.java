package org.example.restaurantmanagement.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

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