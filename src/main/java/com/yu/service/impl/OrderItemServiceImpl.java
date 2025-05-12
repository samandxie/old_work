package com.yu.service.impl;

import com.yu.Mapper.OrderItemMapper;
import com.yu.pojo.OrderItem;
import com.yu.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItem> selectByOrderId(Integer orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }
}
