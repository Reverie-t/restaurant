package org.example.restaurantmanagement.dto;

import lombok.Data;

@Data
public class OrderDetailRequest {

    private Long dishId;

    private Integer quantity;

}