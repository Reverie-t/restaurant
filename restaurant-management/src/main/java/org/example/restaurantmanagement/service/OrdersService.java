package org.example.restaurantmanagement.service;


import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.OrderAddRequest;
import org.example.restaurantmanagement.dto.OrderDetailRequest;
import org.example.restaurantmanagement.dto.TakeawayAddRequest;
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
    public List<OrderVO> listByUserId(Long userId) {
        System.out.println("查询用户订单：" + userId);
        return ordersMapper.listByUserId(userId);

    }
    public List<OrderDetail> detail(Long orderId) {

        return orderDetailMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderDetail>()
                        .eq(OrderDetail::getOrderId, orderId)
        );

    }

    public List<OrderDetail> detail(
            Long orderId,
            Long userId
    ) {

        return ordersMapper
                .listByOrderIdAndUserId(
                        orderId,
                        userId

                );
    }
    @Transactional
    public void updateStatus(Long id, Integer status) {

        // =========================
        // ① 查询订单
        // =========================

        Orders oldOrder = ordersMapper.selectById(id);

        if (oldOrder == null) {
            throw new RuntimeException("订单不存在");
        }


        // =========================
        // ② 检查状态是否合法
        // =========================

        if (status == null ||
                status < 1 ||
                status > 5) {

            throw new RuntimeException(
                    "订单状态不合法"
            );
        }


        // =========================
        // ③ 已完成 / 已取消
        // 不允许再次修改
        // =========================

        if (oldOrder.getStatus() == 4) {

            throw new RuntimeException(
                    "订单已经完成，不能修改状态"
            );
        }

        if (oldOrder.getStatus() == 5) {

            throw new RuntimeException(
                    "订单已经取消，不能修改状态"
            );
        }


        // =========================
        // ④ 检查正常状态流转
        // =========================

        Integer oldStatus =
                oldOrder.getStatus();

        boolean valid = false;


        // 待确认 → 制作中
        if (oldStatus == 1 && status == 2) {
            valid = true;
        }

        // 制作中 → 待取餐/待上菜
        else if (oldStatus == 2 && status == 3) {
            valid = true;
        }

        // 待取餐/待上菜 → 已完成
        else if (oldStatus == 3 && status == 4) {
            valid = true;
        }

        // 待确认 → 已取消
        else if (oldStatus == 1 && status == 5) {
            valid = true;
        }


        if (!valid) {

            throw new RuntimeException(
                    "订单状态不能从 "
                            + oldStatus
                            + " 修改为 "
                            + status
            );
        }


        // =========================
        // ⑤ 修改状态
        // =========================

        Orders orders = new Orders();

        orders.setId(id);

        orders.setStatus(status);

        orders.setUpdateTime(
                LocalDateTime.now()
        );

        int result =
                ordersMapper.updateById(orders);


        if (result == 0) {

            throw new RuntimeException(
                    "订单状态修改失败"
            );
        }


        // =========================
        // ⑥ 堂食完成 / 取消 → 释放餐桌
        // =========================

        if ((status == 4 || status == 5)
                && oldOrder.getOrderType() == 1
                && oldOrder.getTableNo() != null) {

            TableInfo table =
                    tableInfoMapper.selectOne(
                            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TableInfo>()
                                    .eq(
                                            TableInfo::getTableNo,
                                            oldOrder.getTableNo()
                                    )
                    );

            if (table != null) {

                table.setStatus(0);

                tableInfoMapper.updateById(table);

                System.out.println(
                        "餐桌 "
                                + table.getTableNo()
                                + " 已恢复为空闲"
                );
            }
        }
    }
    @Transactional
    public Long add(OrderAddRequest request,Long userId) {

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

        order.setOrderNo(
                System.currentTimeMillis() + ""
        );

        order.setOrderType(
                request.getOrderType()
        );

        order.setUserId(userId);

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
        order.setPickupPhone(request.getPickupPhone());


        // =========================
        // ⑤ 设置总金额
        // =========================

        order.setTotalAmount(totalAmount);


        // =========================
        // ⑥ 设置订单状态
        // =========================

        order.setStatus(1);

        order.setPayStatus(0);


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

        return order.getId();
    }


    /**
     * ============================
     * 模拟支付：将订单标记为已支付
     * ============================
     */
    @Transactional
    public void pay(Long id, Long userId) {

        Orders order = ordersMapper.selectById(id);

        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该订单");
        }

        if (order.getStatus() == 5) {
            throw new RuntimeException("订单已取消，无法支付");
        }

        if (order.getPayStatus() != null && order.getPayStatus() == 1) {
            throw new RuntimeException("订单已支付，请勿重复支付");
        }

        Orders update = new Orders();

        update.setId(id);

        update.setPayStatus(1);

        update.setUpdateTime(LocalDateTime.now());

        ordersMapper.updateById(update);
    }

    /**
     * 将订单标记为"已支付"。
     * <p>
     * 支付宝沙箱支付成功（同步回跳 / 异步通知 / 主动查单）后调用。
     * </p>
     *
     * @param id 订单 ID
     */
    @Transactional
    public void markPaid(Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getPayStatus() != null && order.getPayStatus() == 1) {
            return; // 幂等：已支付不重复处理
        }
        Orders update = new Orders();
        update.setId(id);
        update.setPayStatus(1);
        update.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(update);
    }

    /**
     * 查询订单归属并返回订单金额（用于支付接口校验）。
     */
    public Orders getPayableOrder(Long id, Long userId) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getUserId() == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该订单");
        }
        if (order.getStatus() != null && order.getStatus() == 5) {
            throw new RuntimeException("订单已取消，无法支付");
        }
        if (order.getPayStatus() != null && order.getPayStatus() == 1) {
            throw new RuntimeException("订单已支付，请勿重复支付");
        }
        return order;
    }


    public List<OrderVO> myList(Long userId) {

        return ordersMapper.myList(userId);

    }
    @Transactional
    public void addTakeaway(
            TakeawayAddRequest request
    ) {

        // =========================
        // ① 计算订单总金额
        // =========================

        BigDecimal totalAmount =
                BigDecimal.ZERO;


        for (
                OrderDetailRequest item
                : request.getDetails()
        ) {

            Dish dish =
                    dishMapper.selectById(
                            item.getDishId()
                    );


            if (dish == null) {

                throw new RuntimeException(
                        "菜品不存在：" +
                                item.getDishId()
                );

            }


            if (
                    item.getQuantity() == null ||
                            item.getQuantity() <= 0
            ) {

                throw new RuntimeException(
                        "菜品数量必须大于0"
                );

            }


            totalAmount =
                    totalAmount.add(
                            dish.getPrice()
                                    .multiply(
                                            BigDecimal.valueOf(
                                                    item.getQuantity()
                                            )
                                    )
                    );
        }


        // =========================
        // ② 创建订单
        // =========================

        Orders order =
                new Orders();


        order.setOrderNo(
                System.currentTimeMillis() + ""
        );


        // 外卖
        order.setOrderType(3);


        // 后台人工创建
        // 不绑定当前管理员账号
        order.setUserId(null);


        order.setPickupName(
                request.getPickupName()
        );


        order.setPickupPhone(
                request.getPickupPhone()
        );


        order.setRemark(
                request.getRemark()
        );


        order.setTotalAmount(
                totalAmount
        );


        // 待确认
        order.setStatus(1);


        // 后台代客下单，视为已收款
        order.setPayStatus(1);


        order.setCreateTime(
                LocalDateTime.now()
        );


        order.setUpdateTime(
                LocalDateTime.now()
        );


        // =========================
        // ③ 保存订单
        // =========================

        ordersMapper.insert(order);


        // =========================
        // ④ 保存订单明细
        // =========================

        for (
                OrderDetailRequest item
                : request.getDetails()
        ) {

            Dish dish =
                    dishMapper.selectById(
                            item.getDishId()
                    );


            BigDecimal subtotal =
                    dish.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity()
                                    )
                            );


            OrderDetail detail =
                    new OrderDetail();


            detail.setOrderId(
                    order.getId()
            );


            detail.setDishId(
                    dish.getId()
            );


            detail.setDishName(
                    dish.getName()
            );


            detail.setDishPrice(
                    dish.getPrice()
            );


            detail.setQuantity(
                    item.getQuantity()
            );


            detail.setSubtotal(
                    subtotal
            );


            orderDetailMapper.insert(
                    detail
            );
        }

    }


}