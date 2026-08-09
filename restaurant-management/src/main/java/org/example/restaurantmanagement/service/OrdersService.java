package org.example.restaurantmanagement.service;


import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.OrderAddRequest;
import org.example.restaurantmanagement.dto.OrderDetailRequest;
import org.example.restaurantmanagement.entity.OrderDetail;
import org.example.restaurantmanagement.entity.Orders;
import org.example.restaurantmanagement.mapper.DishMapper;
import org.example.restaurantmanagement.mapper.OrderDetailMapper;
import org.example.restaurantmanagement.mapper.OrdersMapper;
import org.example.restaurantmanagement.mapper.TableInfoMapper;
import org.example.restaurantmanagement.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.restaurantmanagement.entity.TableInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import org.example.restaurantmanagement.entity.Dish;


@Service
@RequiredArgsConstructor
public class OrdersService {


    private final OrdersMapper ordersMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final DishMapper dishMapper;

    private final TableInfoMapper tableInfoMapper;

    public List<OrderVO> list(Integer orderType, Integer status){

        return ordersMapper.list(orderType, status);

    }
    public List<OrderDetail> detail(Long orderId){
        System.out.println(orderDetailMapper.listByOrderId(orderId));
        return orderDetailMapper.listByOrderId(orderId);

    }
    @Transactional
    public void updateStatus(Long id, Integer status) {


        // ① 先查询订单
        Orders oldOrder = ordersMapper.selectById(id);

        if (oldOrder == null) {
            throw new RuntimeException("订单不存在");
        }

        // ② 修改订单状态
        Orders orders = new Orders();

        orders.setId(id);
        orders.setStatus(status);
        orders.setUpdateTime(LocalDateTime.now());

        int result = ordersMapper.updateById(orders);

        System.out.println("数据库更新结果：" + result);

        if (result == 0) {
            throw new RuntimeException("订单状态修改失败");
        }

        // ③ 如果订单完成，并且是堂食订单
        if (status == 4
                && oldOrder.getOrderType() == 1
                && oldOrder.getTableNo() != null) {

            // 根据桌号找到餐桌
            TableInfo table = tableInfoMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TableInfo>()
                            .eq(TableInfo::getTableNo, oldOrder.getTableNo())
            );

            // ④ 找到餐桌后恢复为空闲
            if (table != null) {

                table.setStatus(0);

                tableInfoMapper.updateById(table);

                System.out.println(
                        "餐桌 " + table.getTableNo() + " 已恢复为空闲"
                );
            }
        }
    }

    @Transactional
    public void add(OrderAddRequest request) {

        // =========================
        // ① 计算总金额
        // =========================

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderDetailRequest item : request.getDetails()) {

            // 查询菜品
            Dish dish = dishMapper.selectById(item.getDishId());

            if (dish == null) {
                throw new RuntimeException("菜品不存在：" + item.getDishId());
            }

            // 检查数量
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new RuntimeException("菜品数量必须大于0");
            }

            // 菜品小计
            BigDecimal subtotal = dish.getPrice()
                    .multiply(
                            BigDecimal.valueOf(item.getQuantity())
                    );

            // 累加总金额
            totalAmount = totalAmount.add(subtotal);
        }


        // =========================
        // ② 创建订单
        // =========================

        Orders order = new Orders();

        // 订单编号
        order.setOrderNo(System.currentTimeMillis() + "");

        // 订单类型
        order.setOrderType(request.getOrderType());

        // =========================
        // ③ 堂食 → 根据 tableId 查询桌号
        // =========================

        if (request.getOrderType() == 1) {

            if (request.getTableId() == null) {
                throw new RuntimeException("堂食订单必须选择餐桌");
            }

            TableInfo table = tableInfoMapper.selectById(
                    request.getTableId()
            );

            if (table == null) {
                throw new RuntimeException("餐桌不存在");
            }

            if (table.getStatus() != 0) {
                throw new RuntimeException("该餐桌当前不可用");
            }

            order.setTableNo(table.getTableNo());

            table.setStatus(1);

            tableInfoMapper.updateById(table);
        }


        // =========================
        // ④ 外带
        // =========================

        order.setPickupName(request.getPickupName());


        // =========================
        // ⑤ 设置总金额
        // =========================

        order.setTotalAmount(totalAmount);


        // =========================
        // ⑥ 设置订单状态
        // =========================

        order.setStatus(1);


        // =========================
        // ⑦ 时间
        // =========================

        order.setCreateTime(LocalDateTime.now());

        order.setUpdateTime(LocalDateTime.now());


        // =========================
        // ⑧ 保存订单
        // =========================

        ordersMapper.insert(order);


        // =========================
        // ⑨ 保存订单明细
        // =========================

        for (OrderDetailRequest item : request.getDetails()) {

            Dish dish = dishMapper.selectById(item.getDishId());

            BigDecimal subtotal = dish.getPrice()
                    .multiply(
                            BigDecimal.valueOf(item.getQuantity())
                    );

            OrderDetail detail = new OrderDetail();

            detail.setOrderId(order.getId());

            detail.setDishId(dish.getId());

            detail.setDishName(dish.getName());

            detail.setDishPrice(dish.getPrice());

            detail.setQuantity(item.getQuantity());

            detail.setSubtotal(subtotal);

            orderDetailMapper.insert(detail);
        }
    }


}