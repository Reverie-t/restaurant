package org.example.restaurantmanagement.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;


@Data
@TableName("order_detail")
public class OrderDetail {


    private Long id;


    /**
     * 所属订单
     */
    private Long orderId;


    /**
     * 菜品ID
     */
    private Long dishId;


    /**
     * 下单时菜品名称
     */
    private String dishName;


    /**
     * 下单时价格
     */
    private BigDecimal dishPrice;


    /**
     * 数量
     */
    private Integer quantity;


    /**
     * 小计
     */
    private BigDecimal subtotal;


}