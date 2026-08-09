package org.example.restaurantmanagement.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.OrderAddRequest;
import org.example.restaurantmanagement.dto.OrderStatusRequest;
import org.example.restaurantmanagement.dto.Result;
import org.example.restaurantmanagement.service.OrdersService;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersController {


    private final OrdersService ordersService;


    @GetMapping
    public Result<?> list(
            @RequestParam(required = false) Integer orderType,
            @RequestParam(required = false) Integer status
    ){

        return Result.success(
                ordersService.list(orderType, status)
        );

    }
    @GetMapping("/{id}")
    public Result<?> detail(
            @PathVariable Long id
    ){

        return Result.success(
                ordersService.detail(id)
        );

    }
    @PutMapping("/status")
    public Result<?> updateStatus(
            @RequestBody OrderStatusRequest request){

        ordersService.updateStatus(
                request.getId(),
                request.getStatus());

        return Result.success(null);

    }
    @PostMapping("/order")
    public Result<?> add(@RequestBody @Valid OrderAddRequest request){

        ordersService.add(request);

        return Result.success(null);

    }


}