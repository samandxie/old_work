package com.yu.controller;

import com.yu.pojo.Result;
import com.yu.pojo.Admin;
import com.yu.service.AdminService;
import com.yu.utils.JwtUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/login")
    public Result login(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session){
        Admin record = adminService.selectByName(username);
        if(record == null){
            return Result.error("用户名或密码错误");
        }
        //查询密码是否正确
        if(record.getPassword().equals(password)){
            //登陆成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", "admin");
            claims.put("id", record.getId());
            claims.put("username", record.getUsername());
            String token = JwtUtil.getToken(claims);
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            ops.set(token, token,12, TimeUnit.HOURS);
            session.setAttribute("admin", record);
            return Result.success(token);
        }
        return Result.error("用户名或密码错误");
    }
}
