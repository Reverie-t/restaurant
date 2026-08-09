package org.example.restaurantmanagement.service;

import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.mapper.DashboardMapper;
import org.example.restaurantmanagement.vo.DashboardVO;
import org.example.restaurantmanagement.vo.HotDishVO;
import org.example.restaurantmanagement.vo.OrderTrendVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardMapper dashboardMapper;

    public DashboardVO statistics() {

        return dashboardMapper.statistics();

    }
    public List<HotDishVO> hotDishes() {

        return dashboardMapper.hotDishes();

    }
    public List<OrderTrendVO> orderTrend() {

        return dashboardMapper.orderTrend();

    }

}