package org.example.restaurantmanagement.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderTrendVO {

    /**
     * 时间，例如 10:00
     */
    private String time;

    /**
     * 订单数量
     */
    private Integer orderCount;

    /**
     * 营业额
     */
    private BigDecimal amount;

}