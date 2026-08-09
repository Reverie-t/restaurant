package org.example.restaurantmanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.CategoryAddRequest;
import org.example.restaurantmanagement.dto.CategoryUpdateRequest;
import org.example.restaurantmanagement.dto.Result;
import org.example.restaurantmanagement.service.CategoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public Result<?> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name){

        return Result.success(
                categoryService.list(pageNum,pageSize,name)
        );
    }
    @PostMapping("/category")
    public Result<?> add(@Valid @RequestBody CategoryAddRequest request){

        categoryService.add(request);

        return Result.success(null);
    }
    @PutMapping("/category")
    public Result<?> update(@Valid @RequestBody CategoryUpdateRequest request){
        System.out.println("666");
        categoryService.update(request);
        System.out.println(666);

        return Result.success(null);
    }
    @DeleteMapping("/category/{id}")
    public Result<?> delete(@PathVariable Long id){

        categoryService.delete(id);

        return Result.success(null);
    }
    @GetMapping("/category/simple")
    public Result<?> simple(){

        return Result.success(
                categoryService.simpleList()
        );

    }
}