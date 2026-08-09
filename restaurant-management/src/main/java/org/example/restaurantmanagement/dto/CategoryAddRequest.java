package org.example.restaurantmanagement.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryAddRequest {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    @NotNull(message = "排序不能为空")
    @Min(value = 0, message = "排序不能小于0")
    @Max(value = 9999, message = "排序不能超过9999")
    private Integer sort;

    @NotNull(message = "状态不能为空")
    private Integer status;
}