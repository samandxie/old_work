package com.yu.pojo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Order {
    // 订单id
    private Integer orderId;
    // 用户id
    private Integer userId;
    /*// 收货人姓名
    private String consigneeName;
    // 收货地址
    private String address;
    // 邮政编码
    private String zip;
    // 联系方式
    private String phoneNumber;*/
    // 订单状态
    private Boolean status;
    // 创建时间
    private Date createTime;
    // 用户信息
    private User user_info;
    // 订单项信息
    private List<OrderItem> orderItems;
    // 购物车信息
    private List<ShoppingCart> carts;


    // 收货地址id
    private Integer addressId;
    // 收货地址信息
    private AddressInfo address_info;
}
