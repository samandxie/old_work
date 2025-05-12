package com.yu.service;

import com.yu.pojo.Order;

import java.util.List;

public interface OrderService {

    Order selectByOrderId(Integer orderId);
    List<Order> getAllOrders();


    int deleteByOrderId(Integer id);

    int update(Order order);

    List<Order> searchOrders(Order order);

    int insert(Order order);

    int shipOrder(Integer orderId);
}
