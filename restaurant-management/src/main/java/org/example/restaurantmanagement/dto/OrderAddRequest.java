package org.example.restaurantmanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderAddRequest {

    @NotNull
    private Integer orderType;   /**
     * 订单类型
     *
     * 1 堂食
     * 2 外带
     * 3 外卖
     */

    private Long tableId;

    private Integer people;

    private String pickupName;



    @NotNull
    private List<OrderDetailRequest> details;

}