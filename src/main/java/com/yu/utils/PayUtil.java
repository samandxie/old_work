package com.yu.utils;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.yu.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PayUtil {
    @Autowired
    private OrderService orderService;
    //appid
    private final String APP_ID = "2021000148665147";
    //应用私钥
    private final String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCJU8k0QTk45pLfAZYCeEcvzgXpmPErqIDru11B8C/MOI1l5x6h+aCOgJVM1ArLNKgnu86ucPY8TSqznRPVBR/gWog1i/GEVHnQOI1v/p6aOm9lJFbJwIktop/xChcyNO9ornU++Bx7NRyMBoOxPmAfA+LpfsKsZUj1D/DENIpJrDXpGLB+2BHMft81Y7k2VejMb91OlE5naNDnS18RX1GEZXPvIOdrGqa2Il/jrMOqbyJjheY/TodLQuonl6NznVLzybiHqAFkB6GJJgqpiZYwJrme4/PmW5sYbv1IpgUnWS09PixB6pniTFc7sihew1RUDNI4rAfLq1ngHG62d4KNAgMBAAECggEAB2FLVNWUf6xUhEuPhj50hOsrgixH+bKqiwCcuGhyCSWY47sR+F5z9HSQkXprwhPoXYpIRzOm6AK1osV0RWRrHCGBZG9K4XOwwDHp1/T1DRDK38KeL2V7ecpdkhSavoX9v8BwaJvo2+40xImtFh9mBwSscyPuzlj7HMBojgLM07yrUTDH9otGoO3iDGB1NHboRgrq/qB0+1GcQHKJ6x/g9tpafuTydA10AVw2MiU+yjO++oaz9bqY+jbXdUFSGHog1jgdEB4VotRVkpiLyEh8lE6FczlKMoR2X7gU2iDhbvm9t+ns+nd4ZbhCuv9q6In4FhsxBanYcsk48xHPr47gQQKBgQDGsuq/PKna0xTsDpXwncqkHBc4lauCBX+uKekiVbqBqEQS1ttXqXRlQbio7A5W8TJ2hA+owk6XEGUOihxaCkAB6i2XZ2rFntHN3XYx6KvyOHvpZbG9YaCRogTSn2627KR3AqRz95sVjqUvrqreJZbkAZVYoLIKgDqJFLYH6TnQpQKBgQCw7hFdnBNhjafz3Au+D+Lfu07UAFOs2mGI7pt8uJsQybe9wFy5HWLMhD4iqXMJLxg+GOgtLNv67a/xtGTNIh2xigyzEAT+EPOCDKQwf23Jam5bN7BK4nKTsrYCYLHiz1YGNWSYInIyr8Vb8E6HbZbOOhjBfZnj9D3PLJk6ErAdyQKBgQCW/6i9o9BA52Bi88KmlINGq8JJ0hQAP0WEiX0OBrAxkDoLRxRxTemSeZtnY9yR3cJHppxv1xn6Ww8xOWvKUTfLPLdg88qEW+l81OvZicQB00L3X7zu+p4XjlSz600r3jIt7ugi6OwGOdz2m3pmT5KXu0xPie9DY6cYPZEH+glhsQKBgHcdsD9PhpTU48//w0AQPfPWjPmZxrvGwoeH7NKJDoy84aE0UrimGhAKsDJx1mrGdBHKfvV7EBbd0uhtRLf+UfNnN6mODW5nRym1DQ1BnU5PA0ESOvQPhIf/dx4er2Q9pnLDm5f5CN0rDToajSQMGc15u5T41JRtqPdD1V0Nm9hRAoGBAMUBrtxn6JMf7HA8NvO7oeX7mjcTZicPvir0BXot+HXXrFEmuT8QlznUdYjAyACNpLqApfKiVjhp30MP7NIkNAefA/iZrSIreK0+PfQ9z6z0cUifWe8+6yyYNS9NDXI6VlOreMl3S7ldLtIBa5R+7r/wRvfMfyC8LnPyfh5BLU8G";

    private final String CHARSET = "UTF-8";
    // 支付宝公钥
    private final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnEd88xaXcmOK1AfE26sgmJQ11WLSk5rKZlHjfA7tpWtXvodbmB8iFf/CEoeCxxQvfhMArW5wdH+mKp63tiXOMu23m6Ouka+SUxdLI8pt++ZUSybEcamHXW1vxRk26N+YusBNOUNonyV1KvTN/NwmEYF4YQGMCThz1LELuklLp6bMm+jxFLp95vOoEeqK98xOcBNOHSw8jEOuejIWmJIR8zLbKdgPRErrEWzcyqXwSvc4T3zI76fzqneOcaepVCqfLHoLzgm9bPQ+C06IMYQk4ZUnY+8FTGForCloI510JnK6YjKCsfcGpzE6XcNqsw0DC9IfmehJYoOibkCB2XGM1wIDAQAB";
    //这是沙箱接口路径,正式路径为https://openapi.alipay.com/gateway.do
    private final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private final String FORMAT = "JSON";
    //签名方式
    private final String SIGN_TYPE = "RSA2";
    //支付宝异步通知路径,付款完毕后会异步调用本项目的方法,必须为公网地址
    private final String NOTIFY_URL = "http://y5df8c55.natappfree.cc/api/alipay/toSuccess";
    //支付宝同步通知路径,也就是当付款完毕后跳转本项目的页面,可以不是公网地址
    private String RETURN_URL = "http://localhost:8080/index";
    private AlipayClient alipayClient = null;

    // 添加设置方法
    public void setReturnUrl(String returnUrl) {
        this.RETURN_URL = returnUrl;
    }
    //支付宝官方提供的接口
    public String sendRequestToAlipay(String outTradeNo, Float totalAmount, String subject) throws AlipayApiException {
        //获得初始化的AlipayClient
        alipayClient = new DefaultAlipayClient(GATEWAY_URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
    
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(RETURN_URL);
        alipayRequest.setNotifyUrl(NOTIFY_URL);
    
        // 构造bizContent参数，使用JSONObject确保格式正确
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);  // 注意这里是单数形式
        bizContent.put("total_amount", String.format("%.2f", totalAmount)); // 确保两位小数
        bizContent.put("subject", subject);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        
        alipayRequest.setBizContent(bizContent.toJSONString());
    
        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        System.out.println("返回的结果是："+result );
        return result;
    }

    //    通过订单编号查询
    public String query(String id){
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", id);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = null;
        String body=null;
        try {
            response = alipayClient.execute(request);
            body = response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
        return body;
    }
}
