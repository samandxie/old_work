package com.yu.service;

import com.yu.pojo.Order;

import java.util.List;

public interface OrderHandleService {
    List<Order> getAllOrders();

    void createOrder(Order order);

    List<Order> getOrdersByUserId(Integer userId, Integer page, Integer limit);

    void deleteByOrderId(Integer orderId);
}
