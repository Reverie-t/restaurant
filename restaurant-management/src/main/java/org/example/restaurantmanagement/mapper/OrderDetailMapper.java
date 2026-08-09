package org.example.restaurantmanagement.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.restaurantmanagement.entity.OrderDetail;

import java.util.List;


@Mapper
public interface OrderDetailMapper
        extends BaseMapper<OrderDetail> {


    List<OrderDetail> listByOrderId(
            @Param("orderId") Long orderId
    );


}