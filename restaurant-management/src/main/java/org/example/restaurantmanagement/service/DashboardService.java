package org.example.restaurantmanagement.service;

import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.mapper.DashboardMapper;
import org.example.restaurantmanagement.vo.DashboardVO;
import org.example.restaurantmanagement.vo.HotDishVO;
import org.example.restaurantmanagement.vo.OrderTrendVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardMapper dashboardMapper;

    public DashboardVO statistics() {

        return dashboardMapper.statistics();

    }
    public List<HotDishVO> hotDishes() {

        // ① 查询热销菜品
        List<HotDishVO> hotDishes =
                dashboardMapper.hotDishes();

        // ② 计算每个菜品的销售额
        if (hotDishes != null) {

            for (HotDishVO hotDish : hotDishes) {

                if (hotDish.getPrice() != null
                        && hotDish.getQuantity() != null) {

                    BigDecimal amount =
                            hotDish.getPrice()
                                    .multiply(
                                            BigDecimal.valueOf(
                                                    hotDish.getQuantity()
                                            )
                                    );

                    hotDish.setAmount(amount);
                }
            }
        }

        // ③ 返回已经计算好的结果
        return hotDishes;
    }
    public List<OrderTrendVO> orderTrend() {

        return dashboardMapper.orderTrend();

    }

}