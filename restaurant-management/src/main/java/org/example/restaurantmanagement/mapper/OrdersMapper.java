package org.example.restaurantmanagement.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.restaurantmanagement.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.example.restaurantmanagement.vo.OrderVO;

import java.util.List;


@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
    List<OrderVO> list(
            @Param("orderType") Integer orderType,
            @Param("status") Integer status
    );
}