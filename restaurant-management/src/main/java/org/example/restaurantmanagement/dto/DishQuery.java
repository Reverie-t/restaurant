package org.example.restaurantmanagement.dto;

import lombok.Data;

@Data
public class DishQuery {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String name;

    private Long categoryId;
}