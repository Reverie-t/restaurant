package org.example.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.Result;
import org.example.restaurantmanagement.entity.Ingredient;
import org.example.restaurantmanagement.service.IngredientService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingredient")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;


    /**
     * 查询原材料
     */
    @GetMapping
    public Result<?> list(
            @RequestParam(required = false)
            String name,

            @RequestParam(required = false)
            String category
    ) {

        return Result.success(
                ingredientService.list(
                        name,
                        category
                )
        );
    }


    /**
     * 查询单个原材料
     */
    @GetMapping("/{id}")
    public Result<?> getById(
            @PathVariable Long id
    ) {

        return Result.success(
                ingredientService.getById(id)
        );
    }


    /**
     * 新增原材料
     */
    @PostMapping
    public Result<?> add(
            @RequestBody Ingredient ingredient
    ) {

        ingredientService.add(
                ingredient
        );

        return Result.success(null);
    }


    /**
     * 修改原材料
     */
    @PutMapping
    public Result<?> update(
            @RequestBody Ingredient ingredient
    ) {

        ingredientService.update(
                ingredient
        );

        return Result.success(null);
    }


    /**
     * 删除原材料
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(
            @PathVariable Long id
    ) {

        ingredientService.delete(id);

        return Result.success(null);
    }


    /**
     * 修改状态
     */
    @PutMapping("/status")
    public Result<?> status(
            @RequestParam Long id,
            @RequestParam Integer status
    ) {

        ingredientService.updateStatus(
                id,
                status
        );

        return Result.success(null);
    }
}