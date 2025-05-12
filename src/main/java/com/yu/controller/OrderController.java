package com.yu.controller;

import com.github.pagehelper.PageInfo;
import com.yu.pojo.Book;
import com.yu.pojo.Order;
import com.yu.pojo.Result;
import com.yu.pojo.User;
import com.yu.service.OrderHandleService;
import com.yu.service.OrderService;
import com.yu.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderHandleService orderHandleService;

    @GetMapping("/list")
    public Result getOrderList(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        int offset = (page - 1) * limit;
        List<Order> orders = orderService.getAllOrders();
        if (offset + limit > orders.size())
            limit = orders.size() - offset;
        List<Order> orderList = orders.subList(offset, offset + limit);
        Integer count = orders.size();
        return Result.success(orderList, count);
    }

    @DeleteMapping("{id}")
    public Result delete(@PathVariable Integer id){
        return Result.success(orderService.deleteByOrderId(id));
    }

    @PostMapping("/ship")
    public Result shipOrder(@RequestBody Map<String, Integer> request) {
        Integer orderId = request.get("orderId");
        if(orderId == null) {
            return Result.error("订单ID不能为空");
        }
        return Result.success(orderService.shipOrder(orderId));
    }

    @GetMapping("/search")
    public Result search(Order order, Integer page, Integer limit){
        int offset = (page - 1) * limit;
        List<Order> orders = orderService.searchOrders(order);
        if (offset + limit > orders.size())
            limit = orders.size() - offset;
        List<Order> orderList = orders.subList(offset, offset + limit);
        Integer count = orders.size();
        return Result.success(orderList, count);
    }

    @PostMapping("/submit")
    public Result add(@RequestBody Order order, HttpSession session){
        User user = (User) session.getAttribute("user");
        order.setUserId(user.getUserId());
        order.setCreateTime(new Date());
        order.setStatus(false);
        orderHandleService.createOrder(order);
        return Result.success("/"+user.getUserName()+"/orders");
    }
}
