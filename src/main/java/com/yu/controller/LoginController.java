package com.yu.controller;

import com.yu.pojo.Result;
import com.yu.pojo.User;
import com.yu.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@RestController
public class LoginController {

    @GetMapping("/checkLogIn")
    public Result checkLogIn(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return Result.error("请先登录");
        }
        return Result.success();
    }
}
