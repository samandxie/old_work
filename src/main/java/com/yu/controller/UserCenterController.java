package com.yu.controller;

import com.yu.pojo.Order;
import com.yu.pojo.Result;
import com.yu.pojo.User;
import com.yu.pojo.Validation.UserName;
import com.yu.service.OrderHandleService;
import com.yu.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user_center")
public class UserCenterController {

    @Autowired
    private OrderHandleService orderHandleService;

    @Autowired
    private UserService userService;

    @GetMapping("/orders")
    public Result getUserOrders(Integer page, Integer limit, HttpSession session) {
        User user = (User) session.getAttribute("user");
        int offset = (page - 1) * limit;
        List<Order> orders = orderHandleService.getOrdersByUserId(user.getUserId(), offset, limit);
        if (offset + limit > orders.size())
            limit = orders.size() - offset;
        List<Order> orderList = orders.subList(offset, offset + limit);
        Integer count = orders.size();
        return Result.success(orderList, count);
    }
    @PutMapping("/update")
    public Result updateByUserId(@RequestBody @Validated(UserName.class) User user, HttpSession session){
        User record = (User) session.getAttribute("user");
        user.setUserId(record.getUserId());
        userService.updateByUserId(user);
        return Result.success();
    }

    @DeleteMapping("/orders/{orderId}")
    public Result deleteOrder(@PathVariable Integer orderId) {
        orderHandleService.deleteByOrderId(orderId);
        return Result.success();
    }
}
