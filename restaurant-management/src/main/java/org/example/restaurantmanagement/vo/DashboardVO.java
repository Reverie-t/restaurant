package org.example.restaurantmanagement.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardVO {

    /**
     * 今日订单数
     */
    private Integer todayOrderCount;

    /**
     * 今日营业额
     */
    private BigDecimal todayAmount;

    /**
     * 今日堂食订单
     */
    private Integer todayDineInCount;

    /**
     * 今日外带订单
     */
    private Integer todayTakeoutCount;


    /**
     * 待处理订单
     */
    private Integer pendingOrderCount;

    /**
     * 今日已完成订单
     */
    private Integer completedOrderCount;

}