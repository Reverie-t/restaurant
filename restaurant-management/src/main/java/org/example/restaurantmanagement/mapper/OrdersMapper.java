package org.example.restaurantmanagement.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.restaurantmanagement.entity.OrderDetail;
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
    List<OrderVO> myList(Long userId);
    List<OrderDetail> listByOrderIdAndUserId(
            @Param("orderId") Long orderId,
            @Param("userId") Long userId
    );
    List<OrderVO> listByUserId( @Param("userId") Long userId);
}