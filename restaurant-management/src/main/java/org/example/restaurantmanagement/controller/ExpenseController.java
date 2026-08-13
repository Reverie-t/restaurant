package org.example.restaurantmanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.Result;
import org.example.restaurantmanagement.entity.Expense;
import org.example.restaurantmanagement.service.ExpenseService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expense")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * 查询费用
     */
    @GetMapping
    public Result<?> list(
            @RequestParam(required = false)
            Integer type,

            @RequestParam(required = false)
            String name
    ) {

        return Result.success(
                expenseService.list(type, name)
        );
    }

    /**
     * 新增费用
     */
    @PostMapping
    public Result<?> add(
            @RequestBody @Valid Expense expense
    ) {

        expenseService.add(expense);

        return Result.success(null);
    }

    /**
     * 修改费用
     */
    @PutMapping
    public Result<?> update(
            @RequestBody @Valid Expense expense
    ) {

        expenseService.update(expense);

        return Result.success(null);
    }

    /**
     * 删除费用
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(
            @PathVariable Long id
    ) {

        expenseService.delete(id);

        return Result.success(null);
    }
    @GetMapping("/statistics")
    public Result<?> statistics() {

        return Result.success(
                expenseService.statistics()
        );
    }
}