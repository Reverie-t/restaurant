package org.example.restaurantmanagement.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.math.BigDecimal;

@Data
public class DishUpdateRequest {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Long categoryId;

    @NotNull
    private BigDecimal price;

    private Integer sort;

    private Integer status;

    private String image;

    private String description;
}