package org.example.restaurantmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ingredient")
public class Ingredient {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 原材料名称
     */
    private String name;

    /**
     * 原材料分类
     */
    private String category;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 当前库存
     */
    private BigDecimal stock;

    /**
     * 安全库存
     */
    private BigDecimal safeStock;

    /**
     * 采购单价
     */
    private BigDecimal purchasePrice;

    /**
     * 供应商
     */
    private String supplier;

    /**
     * 状态
     * 1 正常
     * 0 停用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}