package org.example.restaurantmanagement.controller;

import com.alipay.api.AlipayApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.restaurantmanagement.dto.Result;
import org.example.restaurantmanagement.entity.Orders;
import org.example.restaurantmanagement.service.AlipayService;
import org.example.restaurantmanagement.service.OrdersService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝沙箱支付接口
 * <p>
 * 三类入口：
 * <ul>
 *   <li>{@code POST /api/pay/alipay/create/{orderId}} —— 生成支付宝收银台 HTML，前端跳转</li>
 *   <li>{@code GET  /api/pay/alipay/query/{orderId}}  —— 主动查询订单支付状态，前端 returnUrl 回跳后调用</li>
 *   <li>{@code POST /api/pay/alipay/notify}           —— 支付宝异步通知（沙箱+localhost 暂不可达，保留兼容）</li>
 * </ul>
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/pay/alipay")
@RequiredArgsConstructor
public class PayController {

    private final AlipayService alipayService;
    private final OrdersService ordersService;

    /**
     * 创建支付表单（HTML），前端拿到后直接渲染跳转即可。
     */
    @PostMapping("/create/{orderId}")
    public Result<String> createPayForm(
            @PathVariable Long orderId,
            HttpServletRequest request
    ) {
        Long userId = ((Number) request.getAttribute("userId")).longValue();

        Orders order = ordersService.getPayableOrder(orderId, userId);

        try {
            String form = alipayService.createPagePayForm(
                    String.valueOf(order.getId()),
                    order.getTotalAmount(),
                    "MORI 餐厅订单-" + order.getId()
            );
            return Result.success(form);
        } catch (AlipayApiException e) {
            log.error("[Alipay] 创建支付订单失败", e);
            return Result.error("创建支付订单失败：" + e.getMessage());
        }
    }

    /**
     * 主动查询支付状态。前端用户付款完成后跳回商户页面（returnUrl），
     * 调本接口确认是否已支付。
     */
    @GetMapping("/query/{orderId}")
    public Result<Map<String, Object>> queryPayStatus(
            @PathVariable Long orderId,
            HttpServletRequest request
    ) {
        Long userId = ((Number) request.getAttribute("userId")).longValue();

        Orders order = ordersService.getPayableOrder(orderId, userId);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("payStatus", order.getPayStatus() == null ? 0 : order.getPayStatus());

        try {
            boolean paid = alipayService.isPaid(String.valueOf(order.getId()));
            if (paid) {
                ordersService.markPaid(order.getId());
                result.put("payStatus", 1);
            }
            result.put("paid", paid);
            return Result.success(result);
        } catch (AlipayApiException e) {
            log.error("[Alipay] 查询支付状态失败", e);
            return Result.error("查询支付状态失败：" + e.getMessage());
        }
    }

    /**
     * 支付宝异步通知入口（外网回调）。沙箱+localhost 下支付宝无法触达，
     * 但保留该接口以备后续接入内网穿透 / 上线后正式回调使用。
     * <p>
     * 必须返回纯文本 "success" / "fail"，支付宝据此判断是否重试。
     * </p>
     */
    @PostMapping("/notify")
    public String notifyUrl(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            if (v != null && v.length > 0) {
                params.put(k, v[0]);
            }
        });

        log.info("[Alipay] 收到异步通知，参数：{}", params);

        String outTradeNo = params.get("out_trade_no");
        String tradeStatus = params.get("trade_status");

        if (outTradeNo == null || outTradeNo.isEmpty()) {
            return "fail";
        }

        try {
            Long orderId = Long.parseLong(outTradeNo);
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                ordersService.markPaid(orderId);
                log.info("[Alipay] 异步通知标记订单已支付 orderId={}", orderId);
                return "success";
            }
            return "success"; // 其他状态先不处理，避免重复通知刷
        } catch (NumberFormatException e) {
            log.error("[Alipay] 异步通知订单号解析失败：{}", outTradeNo);
            return "fail";
        }
    }
}