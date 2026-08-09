package org.example.restaurantmanagement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.DishAddRequest;
import org.example.restaurantmanagement.dto.DishUpdateRequest;
import org.example.restaurantmanagement.entity.Category;
import org.example.restaurantmanagement.entity.Dish;
import org.example.restaurantmanagement.mapper.CategoryMapper;
import org.example.restaurantmanagement.mapper.DishMapper;
import org.example.restaurantmanagement.vo.DishVO;
import org.example.restaurantmanagement.vo.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.example.restaurantmanagement.dto.DishQuery;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishService {

    private final DishMapper dishMapper;
    private final CategoryMapper categoryMapper;

    public void add(DishAddRequest request) {

        // ① 分类是否存在
        Category category = categoryMapper.selectById(request.getCategoryId());

        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        // ② 同一个分类下菜名不能重复
        Dish exist = dishMapper.selectOne(
                new LambdaQueryWrapper<Dish>()
                        .eq(Dish::getCategoryId, request.getCategoryId())
                        .eq(Dish::getName, request.getName())
        );

        if (exist != null) {
            throw new RuntimeException("该分类下菜品已存在");
        }

        // ③ 保存
        Dish dish = new Dish();

        BeanUtils.copyProperties(request, dish);

        dishMapper.insert(dish);
    }
    public PageResult<DishVO> list(DishQuery query){


        Page<DishVO> page =
                new Page<>(
                        query.getPageNum(),
                        query.getPageSize(),
                        false
                );


        IPage<DishVO> result =
                dishMapper.list(
                        page,
                        query.getName(),
                        query.getCategoryId()
                );
        Long total =
                dishMapper.count(
                        query.getName(),
                        query.getCategoryId()
                );



        return new PageResult<>(
                result.getRecords(),
                total,
                query.getPageNum(),
                query.getPageSize()
        );

    }
    public void update(DishUpdateRequest request){

        Dish dish = new Dish();

        BeanUtils.copyProperties(request,dish);

        dishMapper.updateById(dish);

    }
    public void delete(Long id){

        dishMapper.deleteById(id);

    }
    public void updateStatus(Long id,Integer status){

        Dish dish = new Dish();

        dish.setId(id);

        dish.setStatus(status);

        dishMapper.updateById(dish);

    }
    public void batchDelete(List<Long> ids){

        dishMapper.deleteBatchIds(ids);

    }
}