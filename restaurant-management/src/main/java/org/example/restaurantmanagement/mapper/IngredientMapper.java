package org.example.restaurantmanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.restaurantmanagement.entity.Ingredient;

@Mapper
public interface IngredientMapper
        extends BaseMapper<Ingredient> {

}