package com.yu.controller;

import com.yu.pojo.AddressInfo;
import com.yu.pojo.Result;
import com.yu.pojo.User;
import com.yu.service.AddressInfoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressInfoController {
    @Autowired
    private AddressInfoService addressInfoService;

    @GetMapping("/list")
    public Result selectByUserId(HttpSession session){
        User user = (User) session.getAttribute("user");
        return Result.success(addressInfoService.selectByUserId(user.getUserId()));
    }

    // 新增地址
    @PostMapping("/add")
    public Result addAddress(@RequestBody AddressInfo addressInfo, HttpSession session) {
        User user = (User) session.getAttribute("user");
        addressInfo.setUserId(user.getUserId());
        addressInfoService.insert(addressInfo);
        return Result.success();
    }

    // 修改地址
    @PutMapping("/update")
    public Result updateAddress(@RequestBody AddressInfo addressInfo, HttpSession session) {
        User user = (User) session.getAttribute("user");
        addressInfo.setUserId(user.getUserId());
        addressInfoService.update(addressInfo);
        return Result.success();
    }

    // 删除地址
    @DeleteMapping("/delete/{id}")
    public Result deleteAddress(@PathVariable Integer id) {
        return Result.success(addressInfoService.delete(id));
    }

    // 设置默认地址
    @PutMapping("/setDefault/{id}")
    public Result setDefaultAddress(@PathVariable Integer id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        return Result.success(addressInfoService.setDefault(id, user.getUserId()));
    }

}
