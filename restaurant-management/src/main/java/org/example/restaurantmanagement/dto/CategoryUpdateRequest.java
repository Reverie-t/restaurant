package org.example.restaurantmanagement.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CategoryUpdateRequest {

    @NotNull(message = "分类ID不能为空")
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    @NotNull(message = "排序不能为空")
    @Min(value = 0)
    @Max(value = 9999)
    private Integer sort;

    @NotNull(message = "状态不能为空")
    private Integer status;
}