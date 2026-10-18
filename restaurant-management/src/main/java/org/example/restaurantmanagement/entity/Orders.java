package org.example.restaurantmanagement.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@TableName("orders")
public class Orders {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;


    /**
     * 订单编号
     */
    private String orderNo;


    /**
     * 用户ID
     * 后续可以关联会员表
     */
    private Long userId;


    /**
     * 订单类型
     *
     * 1 堂食
     * 2 外带
     * 3 外卖
     */
    private Integer orderType;


    /**
     * 桌号
     *
     * 堂食使用
     */
    private String tableNo;


    /**
     * 取餐人
     *
     * 外带使用
     */
    private String pickupName;


    /**
     * 联系电话
     *
     * 外带使用
     */
    private String pickupPhone;


    /**
     * 总金额
     */
    private BigDecimal totalAmount;


    /**
     * 订单状态
     *
     * 1 待确认
     * 2 制作中
     * 3 待取餐/待上菜
     * 4 已完成
     * 5 已取消
     */
    private Integer status;


    /**
     * 支付状态
     *
     * 0 未支付
     * 1 已支付
     */
    private Integer payStatus;


    /**
     * 备注
     */
    private String remark;


    private LocalDateTime createTime;


    private LocalDateTime updateTime;

}