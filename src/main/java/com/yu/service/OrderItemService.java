package com.yu.service;


import com.yu.pojo.OrderItem;

import java.util.List;

public interface OrderItemService {

    List<OrderItem> selectByOrderId(Integer orderId);
}
