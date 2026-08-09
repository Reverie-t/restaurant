package org.example.restaurantmanagement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.CategoryAddRequest;
import org.example.restaurantmanagement.dto.CategoryUpdateRequest;
import org.example.restaurantmanagement.entity.Category;
import org.example.restaurantmanagement.mapper.CategoryMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public Page<Category> list(Integer pageNum,
                               Integer pageSize,
                               String name){

        LambdaQueryWrapper<Category> wrapper =
                new LambdaQueryWrapper<>();

        if(name != null && !name.trim().isEmpty()){
            wrapper.like(Category::getName,name.trim());
        }

        wrapper.orderByAsc(Category::getSort);

        return categoryMapper.selectPage(
                new Page<>(pageNum,pageSize),
                wrapper
        );
    }
    public void add(CategoryAddRequest request){

        Category exist = categoryMapper.selectOne(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getName, request.getName())
        );

        if(exist != null){
            throw new RuntimeException("分类名称已存在");
        }

        Category category = new Category();

        BeanUtils.copyProperties(request, category);

        categoryMapper.insert(category);
    }
    public void update(CategoryUpdateRequest request){

        Category exist = categoryMapper.selectById(request.getId());

        if (exist == null) {
            throw new RuntimeException("分类不存在");
        }

        Category repeat = categoryMapper.selectOne(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getName, request.getName())
                        .ne(Category::getId, request.getId())
        );

        if (repeat != null) {
            throw new RuntimeException("分类名称已存在");
        }

        Category category = new Category();

        BeanUtils.copyProperties(request, category);
        System.out.println(category);
        System.out.println(request);

        categoryMapper.updateById(category);
    }
    public void delete(Long id){
        Category category = categoryMapper.selectById(id);

        if(category == null){
            throw new RuntimeException("分类不存在");
        }
        categoryMapper.deleteById(id);

    }
    public List<Category> simpleList(){

        LambdaQueryWrapper<Category> wrapper =
                new LambdaQueryWrapper<>();

        wrapper
                .eq(Category::getStatus,1)
                .orderByAsc(Category::getSort);


        return categoryMapper.selectList(wrapper);

    }


}