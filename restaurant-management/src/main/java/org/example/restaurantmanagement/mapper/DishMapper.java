package org.example.restaurantmanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.restaurantmanagement.entity.Dish;
import org.example.restaurantmanagement.vo.DishVO;
import org.springframework.http.converter.json.GsonBuilderUtils;

import java.util.List;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {


    IPage<DishVO> list(
            Page<DishVO> page,
            String name,
            Long categoryId
    );
    Long count(
            String name,
            Long categoryId
    );
    List<DishVO> listAllForMenu();
}