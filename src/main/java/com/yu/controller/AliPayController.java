package com.yu.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.yu.pojo.Order;
import com.yu.pojo.OrderItem;
import com.yu.pojo.User;
import com.yu.service.OrderItemService;
import com.yu.service.OrderService;
import com.yu.utils.PayUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/api/alipay")
public class AliPayController {
    @Autowired
    private PayUtil payUtil;
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService  orderItemService;

    private Order oorders = null;
    private String tokens = "";

    @ResponseBody
    @PostMapping("/pay")
    public String alipay(@RequestBody Order order, HttpSession session) throws AlipayApiException {
        try {
            // 1. 验证订单数据
            System.out.println(order.getOrderItems());
            if(order == null || order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
                throw new IllegalArgumentException("订单数据不完整");
            }
            
            // 2. 计算总金额
            BigDecimal total = order.getOrderItems().stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            // 3. 生成纯数字订单号(保持为String类型以满足支付宝要求)
            String outTradeNo = String.format("%010d", 
            (System.currentTimeMillis() % 1000000000L) + 
            (long)(Math.random() * 10000));

            // 添加订单号验证
            if(!outTradeNo.matches("^\\d{10}$")) {
            throw new IllegalArgumentException("订单号格式不正确");
            }
            User user = (User) session.getAttribute("user");
            payUtil.setReturnUrl("http://localhost:8080/" + user.getUserName() + "/orders");
                
            // 4. 调用支付宝
            return payUtil.sendRequestToAlipay(outTradeNo, total.floatValue(), "实体书");
        } catch (Exception e) {
            // 返回错误信息给前端
            return "{\"code\":400,\"message\":\"" + e.getMessage() + "\"}";
        }
    }   

    //    当我们支付完成之后跳转这个请求并携带参数，我们将里面的订单id接收到，通过订单id查询订单信息，信息包括支付是否成功等
    @GetMapping("/toSuccess")
    public String returns(String out_trade_no,HttpSession session) throws ParseException {
        String query = payUtil.query(out_trade_no);
        System.out.println("==>" + query);
        JSONObject jsonObject = JSONObject.parseObject(query);
        Object o = jsonObject.get("alipay_trade_query_response");
        Map map = (Map) o;
        System.out.println(map);
        Object s = map.get("trade_status");
        if (s.equals("TRADE_SUCCESS")) {
            //当支付成功之后要执行的操作
            System.out.println("订单支付成功");

            System.out.println(s);
            return "redirect:http://localhost:8081/#/paysuccess";
        } else {
//            支付失败要执行的操作
            System.out.println("订单支付失败");
            return "index";
        }
    }

    /*
参数1：订单号
参数2：订单金额
参数3：订单名称
 */

}