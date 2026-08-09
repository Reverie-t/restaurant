package org.example.restaurantmanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.restaurantmanagement.entity.Category;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}