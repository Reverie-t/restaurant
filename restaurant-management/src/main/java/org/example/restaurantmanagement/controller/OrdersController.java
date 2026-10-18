package org.example.restaurantmanagement.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.OrderAddRequest;
import org.example.restaurantmanagement.dto.OrderStatusRequest;
import org.example.restaurantmanagement.dto.Result;
import org.example.restaurantmanagement.dto.TakeawayAddRequest;
import org.example.restaurantmanagement.service.OrdersService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;


    /**
     * ============================
     * 查询所有订单
     * 管理员使用
     * ============================
     */
    @GetMapping
    public Result<?> list(
            @RequestParam(required = false) Integer orderType,
            @RequestParam(required = false) Integer status
    ) {

        return Result.success(
                ordersService.list(orderType, status)
        );
    }


    /**
     * ============================
     * 查询当前登录顾客的订单
     * 顾客使用
     * ============================
     */
    @GetMapping("/my")
    public Result<?> myOrders(
            HttpServletRequest request
    ) {

        Long userId = ((Number) request
                .getAttribute("userId"))
                .longValue();

        System.out.println(
                "查询我的订单，用户ID：" + userId
        );

        return Result.success(
                ordersService.listByUserId(userId)
        );
    }


    /**
     * ============================
     * 管理员查询订单详情
     * 可以查看任意订单
     * ============================
     */
    @GetMapping("/{id}")
    public Result<?> detail(
            @PathVariable Long id
    ) {

        return Result.success(
                ordersService.detail(id)
        );
    }


    /**
     * ============================
     * 顾客查询自己的订单详情
     * 只能查看属于自己的订单
     * ============================
     */
    @GetMapping("/my/{id}")
    public Result<?> myDetail(
            @PathVariable Long id,
            HttpServletRequest request
    ) {

        Long userId = ((Number) request
                .getAttribute("userId"))
                .longValue();

        System.out.println(
                "查询我的订单详情，用户ID："
                        + userId
                        + "，订单ID："
                        + id
        );

        return Result.success(
                ordersService.detail(id, userId)
        );
    }


    /**
     * ============================
     * 修改订单状态
     * 管理员使用
     * ============================
     */
    @PutMapping("/status")
    public Result<?> updateStatus(
            @RequestBody OrderStatusRequest request
    ) {

        ordersService.updateStatus(
                request.getId(),
                request.getStatus()
        );

        return Result.success(null);
    }


    /**
     * ============================
     * 创建订单
     * 顾客使用
     * ============================
     */
    @PostMapping("/order")
    public Result<?> add(
            @RequestBody OrderAddRequest request,
            HttpServletRequest httpRequest
    ) {

        Long userId = ((Number) httpRequest
                .getAttribute("userId"))
                .longValue();

        System.out.println(
                "当前登录用户ID：" + userId
        );

        Long orderId = ordersService.add(
                request,
                userId
        );

        // 雪花 ID 超过 JS 安全整数范围，必须转成字符串返回，避免前端精度丢失
        return Result.success(String.valueOf(orderId));
    }


    /**
     * ============================
     * 模拟支付
     * 顾客使用
     * ============================
     */
    @PostMapping("/pay/{id}")
    public Result<?> pay(
            @PathVariable Long id,
            HttpServletRequest request
    ) {

        Long userId = ((Number) request
                .getAttribute("userId"))
                .longValue();

        ordersService.pay(id, userId);

        return Result.success(null);
    }
    @PostMapping("/takeaway")
    public Result<?> addTakeaway(
            @RequestBody @Valid TakeawayAddRequest request
    ) {

        ordersService.addTakeaway(request);

        return Result.success(null);
    }
}