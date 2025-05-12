package com.yu.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {
    // 订单项id
    private Integer orderItemId;
    // 订单id
    private Integer orderId;
    // 图书id
    private Integer bookId;
    // 图书价格
    private BigDecimal price;
    // 图书数量
    private Integer quantity;
    // 图书信息
    private Book book_info;
}
