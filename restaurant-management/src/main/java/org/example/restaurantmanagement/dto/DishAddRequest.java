package org.example.restaurantmanagement.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DishAddRequest {

    @NotBlank(message = "菜品名称不能为空")
    private String name;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "排序不能为空")
    @Min(value = 0)
    private Integer sort;

    private String image;

    private String description;

    @NotNull(message = "状态不能为空")
    private Integer status;
}