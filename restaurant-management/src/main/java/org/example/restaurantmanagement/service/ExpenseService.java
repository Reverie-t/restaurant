package org.example.restaurantmanagement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.entity.Expense;
import org.example.restaurantmanagement.mapper.ExpenseMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseMapper expenseMapper;

    /**
     * 查询费用
     */
    public List<Expense> list(
            Integer type,
            String name
    ) {

        LambdaQueryWrapper<Expense> wrapper =
                new LambdaQueryWrapper<>();

        if (type != null) {

            wrapper.eq(
                    Expense::getType,
                    type
            );

        }

        if (name != null && !name.isBlank()) {

            wrapper.like(
                    Expense::getName,
                    name
            );

        }

        wrapper.orderByDesc(
                Expense::getExpenseDate
        );

        return expenseMapper.selectList(wrapper);
    }

    /**
     * 新增费用
     */
    public void add(Expense expense) {

        expenseMapper.insert(expense);
    }

    /**
     * 修改费用
     */
    public void update(Expense expense) {

        if (expense.getId() == null) {
            throw new RuntimeException("费用ID不能为空");
        }

        Expense old =
                expenseMapper.selectById(
                        expense.getId()
                );

        if (old == null) {
            throw new RuntimeException("费用不存在");
        }

        expenseMapper.updateById(expense);
    }

    /**
     * 删除费用
     */
    public void delete(Long id) {

        Expense expense =
                expenseMapper.selectById(id);

        if (expense == null) {
            throw new RuntimeException("费用不存在");
        }

        expenseMapper.deleteById(id);
    }
    public BigDecimal getCurrentMonthTotal() {

        return expenseMapper.getCurrentMonthTotal();
    }
    public Map<String, Object> statistics() {

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "total",
                expenseMapper.getCurrentMonthTotal()
        );

        result.put(
                "labor",
                expenseMapper.getCurrentMonthByType(1)
        );

        result.put(
                "water",
                expenseMapper.getCurrentMonthByType(2)
        );

        result.put(
                "electricity",
                expenseMapper.getCurrentMonthByType(3)
        );

        result.put(
                "gas",
                expenseMapper.getCurrentMonthByType(4)
        );

        result.put(
                "administrative",
                expenseMapper.getCurrentMonthByType(5)
        );

        result.put(
                "other",
                expenseMapper.getCurrentMonthByType(6)
        );

        return result;
    }
}