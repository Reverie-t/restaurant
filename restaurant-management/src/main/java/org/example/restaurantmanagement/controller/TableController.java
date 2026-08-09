package org.example.restaurantmanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.Result;
import org.example.restaurantmanagement.dto.TableAddRequest;
import org.example.restaurantmanagement.dto.TableStatusRequest;
import org.example.restaurantmanagement.dto.TableUpdateRequest;
import org.example.restaurantmanagement.entity.TableInfo;
import org.example.restaurantmanagement.mapper.TableInfoMapper;
import org.example.restaurantmanagement.service.TableService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/table")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    private final TableInfoMapper tableInfoMapper;
    @GetMapping
    public Result<?> list(

            @RequestParam(required = false) String tableNo,

            @RequestParam(required = false) Integer status,

            @RequestParam(required = false) String area){

        return Result.success(

                tableService.list(
                        tableNo,
                        status,
                        area
                )

        );

    }
    @PostMapping
    public Result<?> add(
            @RequestBody @Valid TableAddRequest request){

        tableService.add(request);

        return Result.success(null);

    }
    @PutMapping
    public Result<?> update(
            @RequestBody @Valid TableUpdateRequest request){

        tableService.update(request);

        return Result.success(null);

    }
    @DeleteMapping("/{id}")
    public Result<?> delete(
            @PathVariable Long id){

        tableService.delete(id);

        return Result.success(null);

    }
    @PutMapping("/status")
    public Result<?> status(
            @RequestBody TableStatusRequest request){

        tableService.updateStatus(
                request.getId(),
                request.getStatus());

        return Result.success(null);

    }
}
