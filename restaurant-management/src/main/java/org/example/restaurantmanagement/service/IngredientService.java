package org.example.restaurantmanagement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.entity.Ingredient;
import org.example.restaurantmanagement.mapper.IngredientMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientMapper ingredientMapper;


    /**
     * 查询原材料
     *
     * name：名称，可选
     * category：分类，可选
     */
    public List<Ingredient> list(
            String name,
            String category
    ) {

        LambdaQueryWrapper<Ingredient> wrapper =
                new LambdaQueryWrapper<>();

        // 按名称查询
        if (name != null && !name.isBlank()) {

            wrapper.like(
                    Ingredient::getName,
                    name
            );
        }

        // 按分类查询
        if (category != null && !category.isBlank()) {

            wrapper.eq(
                    Ingredient::getCategory,
                    category
            );
        }

        // 最新添加的在前面
        wrapper.orderByDesc(
                Ingredient::getCreateTime
        );

        return ingredientMapper.selectList(wrapper);
    }


    /**
     * 查询单个原材料
     */
    public Ingredient getById(Long id) {

        Ingredient ingredient =
                ingredientMapper.selectById(id);

        if (ingredient == null) {

            throw new RuntimeException(
                    "原材料不存在"
            );
        }

        return ingredient;
    }


    /**
     * 新增原材料
     */
    public void add(Ingredient ingredient) {

        // 名称不能为空
        if (ingredient.getName() == null
                || ingredient.getName().isBlank()) {

            throw new RuntimeException(
                    "原材料名称不能为空"
            );
        }

        // 单位不能为空
        if (ingredient.getUnit() == null
                || ingredient.getUnit().isBlank()) {

            throw new RuntimeException(
                    "计量单位不能为空"
            );
        }

        // 库存不能小于 0
        if (ingredient.getStock() != null
                && ingredient.getStock().signum() < 0) {

            throw new RuntimeException(
                    "库存不能小于0"
            );
        }

        // 安全库存不能小于 0
        if (ingredient.getSafeStock() != null
                && ingredient.getSafeStock().signum() < 0) {

            throw new RuntimeException(
                    "安全库存不能小于0"
            );
        }

        // 默认状态：正常
        if (ingredient.getStatus() == null) {

            ingredient.setStatus(1);
        }

        ingredientMapper.insert(ingredient);
    }


    /**
     * 修改原材料
     */
    public void update(Ingredient ingredient) {

        if (ingredient.getId() == null) {

            throw new RuntimeException(
                    "原材料ID不能为空"
            );
        }

        Ingredient old =
                ingredientMapper.selectById(
                        ingredient.getId()
                );

        if (old == null) {

            throw new RuntimeException(
                    "原材料不存在"
            );
        }

        if (ingredient.getName() == null
                || ingredient.getName().isBlank()) {

            throw new RuntimeException(
                    "原材料名称不能为空"
            );
        }

        if (ingredient.getUnit() == null
                || ingredient.getUnit().isBlank()) {

            throw new RuntimeException(
                    "计量单位不能为空"
            );
        }

        if (ingredient.getStock() != null
                && ingredient.getStock().signum() < 0) {

            throw new RuntimeException(
                    "库存不能小于0"
            );
        }

        if (ingredient.getSafeStock() != null
                && ingredient.getSafeStock().signum() < 0) {

            throw new RuntimeException(
                    "安全库存不能小于0"
            );
        }

        ingredientMapper.updateById(ingredient);
    }


    /**
     * 删除原材料
     */
    public void delete(Long id) {

        Ingredient ingredient =
                ingredientMapper.selectById(id);

        if (ingredient == null) {

            throw new RuntimeException(
                    "原材料不存在"
            );
        }

        ingredientMapper.deleteById(id);
    }


    /**
     * 修改状态
     */
    public void updateStatus(
            Long id,
            Integer status
    ) {

        if (status == null
                || (status != 0 && status != 1)) {

            throw new RuntimeException(
                    "状态不合法"
            );
        }

        Ingredient ingredient =
                ingredientMapper.selectById(id);

        if (ingredient == null) {

            throw new RuntimeException(
                    "原材料不存在"
            );
        }

        Ingredient update =
                new Ingredient();

        update.setId(id);
        update.setStatus(status);

        ingredientMapper.updateById(update);
    }
}