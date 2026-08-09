package org.example.restaurantmanagement.vo;

import lombok.Data;

@Data
public class HotDishVO {

    /**
     * 菜品ID
     */
    private Long dishId;

    /**
     * 菜品名称
     */
    private String dishName;

    /**
     * 销售数量
     */
    private Integer quantity;

}