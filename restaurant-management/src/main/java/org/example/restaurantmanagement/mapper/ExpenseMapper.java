package org.example.restaurantmanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.restaurantmanagement.entity.Expense;

import java.math.BigDecimal;

@Mapper
public interface ExpenseMapper extends BaseMapper<Expense> {

    /**
     * 查询本月总费用
     */
    @Select("""
        SELECT COALESCE(SUM(amount), 0)
        FROM expense
        WHERE expense_date >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
        AND expense_date < DATE_ADD(
            DATE_FORMAT(CURDATE(), '%Y-%m-01'),
            INTERVAL 1 MONTH
        )
    """)
    BigDecimal getCurrentMonthTotal();

    /**
     * 查询本月某类型费用
     */
    @Select("""
        SELECT COALESCE(SUM(amount), 0)
        FROM expense
        WHERE type = #{type}
        AND expense_date >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
        AND expense_date < DATE_ADD(
            DATE_FORMAT(CURDATE(), '%Y-%m-01'),
            INTERVAL 1 MONTH
        )
    """)
    BigDecimal getCurrentMonthByType(Integer type);
}