package org.example.restaurantmanagement.vo;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class DishVO {
    private Long id;

    private String name;

    private String categoryName;

    private BigDecimal price;

    private Integer sort;

    private Integer status;

    private String image;

    private String description;
}
