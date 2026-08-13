package org.example.restaurantmanagement.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class OrderVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    private String orderNo;


    /**
     * 堂食/外带
     */
    private String orderTypeName;


    private BigDecimal totalAmount;



    private Integer status;


    /**
     * 状态名称
     */
    private String statusName;


    private String tableNo;


    private String pickupName;

    private String pickupPhone;


    private LocalDateTime createTime;


}