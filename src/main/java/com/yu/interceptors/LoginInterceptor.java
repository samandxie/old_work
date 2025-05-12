package com.yu.interceptors;

import com.yu.utils.JwtUtil;
import com.yu.utils.ThreadLocalUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //令牌验证 cookie方式
        String token = getToken(request, response);
        try{
            Map<String, Object> claims = JwtUtil.parseToken(token);
            if (claims.get("role") != null)
                return true;
            else throw new RuntimeException("令牌错误");
        } catch (Exception e){
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        //清空ThreadLocal中的数据
        ThreadLocalUtil.remove();
    }

    String getToken(HttpServletRequest request,  HttpServletResponse response) {
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
        if (token == null){
            response.setStatus(401);
            throw new RuntimeException("令牌错误");
        }
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String redisToken = ops.get(token);
        if (redisToken == null){
            response.setStatus(401);
            throw new RuntimeException("令牌错误");
        }
        return token;
    }
}
