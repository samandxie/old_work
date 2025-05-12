package com.yu.service.impl;

import com.yu.Mapper.BookMapper;
import com.yu.Mapper.OrderItemMapper;
import com.yu.Mapper.OrderMapper;
import com.yu.Mapper.ShoppingCartMapper;
import com.yu.exception.CustomizeException;
import com.yu.pojo.*;
import com.yu.service.OrderHandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderHandleServiceImpl implements OrderHandleService {

    @Autowired
    BookMapper bookMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Override
    public List<Order> getAllOrders() {
        return orderMapper.getAllOrders();
    }

    @Override
    public void createOrder(Order order) {
        orderMapper.insert(order);
        Integer orderId = order.getOrderId();
        for (OrderItem orderItem :order.getOrderItems()) {
            orderItem.setOrderId(orderId);
            orderItemMapper.insert(orderItem);
            //如果存在购物车，则删除购物车
            shoppingCartMapper.deleteByUserIdAndBookId(order.getUserId(), orderItem.getBookId());
            //判断书籍库存
            Book book = bookMapper.selectByBookId(orderItem.getBookId());
            if(book.getStock()<orderItem.getQuantity()){
                //库存不足
                throw new CustomizeException("书籍《"+book.getBookName()+"》的库存不足");
            }
            //修改书籍库存
            book.setStock(book.getStock()-orderItem.getQuantity());
            bookMapper.updateByBookId(book);
        }
    }

    @Override
    public List<Order> getOrdersByUserId(Integer userId, Integer page, Integer limit) {
        return orderMapper.selectByUserId(userId);
    }

    @Override
    public void deleteByOrderId(Integer orderId) {
        orderMapper.deleteByOrderId(orderId);
    }
}
