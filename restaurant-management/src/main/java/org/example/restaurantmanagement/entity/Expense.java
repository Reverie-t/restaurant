package org.example.restaurantmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("expense")
public class Expense {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 费用名称
     */
    private String name;

    /**
     * 费用类型
     *
     * 1 人工费
     * 2 水费
     * 3 电费
     * 4 燃气费
     * 5 行政管理费
     * 6 其他
     */
    private Integer type;

    /**
     * 费用金额
     */
    private BigDecimal amount;

    /**
     * 费用发生日期
     */
    private LocalDate expenseDate;

    /**
     * 经办人
     */
    private String operator;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}