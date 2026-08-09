package org.example.restaurantmanagement.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.restaurantmanagement.vo.DashboardVO;
import org.example.restaurantmanagement.vo.HotDishVO;
import org.example.restaurantmanagement.vo.OrderTrendVO;

import java.util.List;

@Mapper
public interface DashboardMapper {

    DashboardVO statistics();
    List<HotDishVO> hotDishes();
    List<OrderTrendVO> orderTrend();

}
