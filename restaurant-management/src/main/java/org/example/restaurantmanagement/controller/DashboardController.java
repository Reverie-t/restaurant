package org.example.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.Result;
import org.example.restaurantmanagement.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/statistics")
    public Result<?> statistics() {

        return Result.success(
                dashboardService.statistics()
        );

    }
    @GetMapping("/hot-dishes")
    public Result<?> hotDishes() {

        return Result.success(
                dashboardService.hotDishes()
        );

    }
    @GetMapping("/order-trend")
    public Result<?> orderTrend() {

        return Result.success(
                dashboardService.orderTrend()
        );

    }

}