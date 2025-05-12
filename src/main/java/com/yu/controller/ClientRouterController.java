package com.yu.controller;

import com.yu.pojo.Book;
import com.yu.pojo.User;
import com.yu.utils.ThreadLocalUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class ClientRouterController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @GetMapping("/login")
    public String toLogin(){
        return "login";
    }

    @GetMapping("/register")
    public String toRegister(){
        return "register";
    }

    @GetMapping("/index")
    public String toIndex(){
        return "index";
    }

    @GetMapping("/{userName}/user_center")
    public String toUserCenter(@PathVariable String userName, Model model, HttpSession session){
        model.addAttribute("user", session.getAttribute("user"));
        return "user_center";
    }

    @GetMapping("/{userName}/user_center_orders")
    public String toUserCenterOrders(@PathVariable String userName, Model model, HttpSession session){
        model.addAttribute("user", session.getAttribute("user"));
        return "user_center_orders";
    }

    @GetMapping("/{userName}/user_center_address")
    public String toUserCenterAddress(@PathVariable String userName, Model model, HttpSession session){
        model.addAttribute("user", session.getAttribute("user"));
        return "user_center_address";
    }

    @GetMapping("/admin/login")
    public String toAdminLogin(){
        return "admin/login";
    }
    @GetMapping("/admin/index")
    public String toAdminIndex(){
        return "admin/index";
    }

    @GetMapping("/admin/book_manage")
    public String toAdminFragment(){
        return "admin/books";
    }

    @GetMapping("/admin/category_manage")
    public String toCategoryManage(){
        return "admin/category";
    }

    @GetMapping("/admin/user_manage")
    public String toUserManage(){
        return "admin/user";
    }

    @GetMapping("/admin/order_manage")
    public String toOrderManage(){
        return "admin/order";
    }

    @GetMapping("/admin/add_book")
    public String toAddBook(){
        return "admin/add_book";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request){
        if (session.getAttribute("user") != null)
            session.removeAttribute("user");
        if (session.getAttribute("admin") != null)
            session.removeAttribute("admin");
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        // 根据 Token 从 Redis 中删除对应的键值对
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (ops.getOperations().hasKey(token)) {
            redisTemplate.delete(token);
        }
        return "redirect:/login";
    }

    @GetMapping("/{username}/orders")
    public String toOrderCenter(@PathVariable String username,HttpSession session){
        User user = (User) session.getAttribute("user");
        if(user!=null && user.getUserName().equals(username)){
            return "user_orders";
        }
        return "redirect:/login";
    }

    @GetMapping("/{username}/shopping_cart")
    public String toShoppingCart(@PathVariable String username,HttpSession session){
        User user = (User) session.getAttribute("user");
        if(user!=null && user.getUserName().equals(username)){
            return "shopping_cart";
        }
        return "redirect:/login";
    }
}
