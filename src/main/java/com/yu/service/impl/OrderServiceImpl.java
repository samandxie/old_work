package com.yu.service.impl;

import com.yu.Mapper.OrderMapper;
import com.yu.exception.CustomizeException;
import com.yu.pojo.Order;
import com.yu.service.OrderService;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Order selectByOrderId(Integer orderId) {
        return orderMapper.selectByOrderId(orderId);
    }

    public List<Order> getAllOrders() {
        return orderMapper.getAllOrders();
    }

    @Override
    public int deleteByOrderId(Integer id) {
        return orderMapper.deleteByOrderId(id);

    }

    @Override
    public int update(Order order) {
        return orderMapper.update(order);
    }

    @Override
    public List<Order> searchOrders(Order order) {
        return orderMapper.searchOrders(order);
    }

    @Override
    public int insert(Order order) {
        return orderMapper.insert(order);
    }

    @Override
    public int shipOrder(Integer orderId) {
        Order order = orderMapper.selectByOrderId(orderId);
        if(order == null) {
            throw new CustomizeException("订单不存在");
        }
        if(order.getStatus()) {
            throw new CustomizeException("订单已发货，请勿重复操作");
        }
        order.setStatus(true);
        return orderMapper.updateStatusByOrderId(orderId);
    }
}
