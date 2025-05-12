package com.yu.controller;

import com.yu.pojo.Book;
import com.yu.pojo.Result;
import com.yu.pojo.User;
import com.yu.pojo.Validation.UserName;
import com.yu.pojo.Validation.Password;
import com.yu.service.UserService;
import com.yu.utils.JwtUtil;
import com.yu.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpSession;
import org.apache.ibatis.annotations.Delete;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/register")
    public Result register(@Validated({UserName.class, Password.class})User user){
        //查询用户名是否已存在
        User record = userService.findByName(user.getUserName());
        if(record == null){
            //用户名不存在
            //注册
            userService.register(user);
            return Result.success();
        }else {
            //用户名已存在
            return Result.error("用户名已存在");
        }
    }

    @PostMapping("/login")
    public Result<String> login(@Validated({UserName.class, Password.class}) User user ,HttpSession session){
        //查询用户名是否正确
        User record = userService.findByName(user.getUserName());
        if(record == null){
            return Result.error("用户名或密码错误");
        }
        //查询密码是否正确
        if(record.getPassword().equals(user.getPassword())){
            //登陆成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", "user");
            claims.put("id", record.getUserId());
            claims.put("username", record.getUserName());
            ThreadLocalUtil.set(record);
            String token = JwtUtil.getToken(claims);
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            ops.set(token, token,12, TimeUnit.HOURS);
            session.setAttribute("user", record);
            return Result.success(token);
        }

        return Result.error("用户名或密码错误");
    }

    @GetMapping("/userInfo")
    public Result<User> userInfo(){
        //根据用户名查询用户信息
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User user = userService.findByName(username);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result updateByUser(@RequestBody @Validated(UserName.class) User user){
        userService.updateByUser(user);
        return Result.success(user);
    }

    @PutMapping("/updateByAdmin")
    public Result updateByAdmin(@RequestBody @Validated({UserName.class, Password.class}) User user){
        User record = userService.findByName(user.getUserName());
        if(record == null){
            //用户名不存在
            userService.updateByAdmin(user);
            return Result.success();
        }else {
            //用户名已存在
            return Result.error("用户名不能重复");
        }
    }

    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl,HttpSession session){
        User user = (User) session.getAttribute("user");
        userService.updateAvatar(avatarUrl, user.getUserId());
        return Result.success();
    }

    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> params,@RequestHeader("Authorization") String token){
        String odlPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        String username = (String) map.get("username");
        if (!StringUtils.hasLength(odlPwd)||!StringUtils.hasLength(newPwd)||!StringUtils.hasLength(rePwd)){
            return Result.error("参数错误");
        }
        if (!newPwd.equals(rePwd)){
            return Result.error("两次密码不一致");
        }
        User loginUser = userService.findByName(username);
        if(!loginUser.getPassword().equals(odlPwd)){
            return Result.error("原密码错误");
        }
        userService.updatePwd(newPwd, id);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.getOperations().delete(token);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<User>> selectAllUser(@RequestParam("page") int page, @RequestParam("limit") int limit){
        int offset = (page - 1) * limit;
        List<User> users = userService.selectAllUser();
        if (offset + limit > users.size())
            limit = users.size() - offset;
        List<User> userList = users.subList(offset, offset + limit);
        Integer count = users.size();
        return Result.success(userList, count);
    }

    @DeleteMapping("{id}")
    public Result deleteById(@PathVariable Integer id){
        userService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/search")
    public Result search(User user, Integer page, Integer limit){
        List<User> users = userService.selectByUser(user);
        return Result.success(users);
    }


}

