package org.example.restaurantmanagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class TakeawayAddRequest {

    /**
     * 取餐人
     */
    @NotBlank(message = "请输入取餐人")
    private String pickupName;

    /**
     * 联系电话
     */
    @NotBlank(message = "请输入联系电话")
    private String pickupPhone;

    /**
     * 备注
     */
    private String remark;

    /**
     * 菜品明细
     */
    @NotEmpty(message = "请至少选择一道菜品")
    @Valid
    private List<OrderDetailRequest> details;
}