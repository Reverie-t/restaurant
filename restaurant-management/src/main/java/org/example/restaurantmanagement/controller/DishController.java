package org.example.restaurantmanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.*;
import org.example.restaurantmanagement.service.DishService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin
public class DishController {

    private final DishService dishService;

    @PostMapping("/dish")
    public Result<?> add(@Valid @RequestBody DishAddRequest request){

        dishService.add(request);

        return Result.success(null);
    }
    @GetMapping("/dish")
    public Result<?> list(DishQuery query){

        return Result.success(
                dishService.list(query)
        );

    }
    @PutMapping("/dish")
    public Result<?> update(@RequestBody @Valid DishUpdateRequest request){

        dishService.update(request);

        return Result.success(null);

    }
    @DeleteMapping("/dish/{id}")
    public Result<?> delete(@PathVariable Long id){

        dishService.delete(id);

        return Result.success(null);

    }
    @PutMapping("/dish/status")
    public Result<?> status(@RequestBody DishStatusRequest request){

        dishService.updateStatus(

                request.getId(),

                request.getStatus());

        return Result.success(null);

    }
    @DeleteMapping("/dish/batch")
    public Result<?> batchDelete(
            @RequestBody List<Long> ids
    ){

        dishService.batchDelete(ids);

        return Result.success(null);

    }

}