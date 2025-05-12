package com.yu.pojo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShoppingCart {
    // 购物车ID
    private Integer cartId;
    // 用户ID
    private Integer userId;
    // 图书ID
    @NotNull(message = "商品ID不能为空")
    private Integer bookId;
    // 价格
    private BigDecimal price;
    // 购买数量
    @Min(value = 1,message = "购买数量不能小于或等于0")
    @Max(value = 10,message = "每个用户限购10件")
    private Integer quantity;
    // 图书信息
    private Book book_info;
}
