package com.yu.service;


import com.yu.pojo.Result;
import com.yu.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    //根据用户名查询用户
    public User findByName(String username);

    //注册
    public void register(User user);

    //更新
    void updateByUser(User user);
    void updateByUserId(User user);

    int updateAvatar(String avatarUrl,Integer userId);

    void updatePwd(String newPwd, Integer id);

    List<User> selectAllUser();

    void updateByAdmin(User user);

    void deleteById(Integer id);

    List<User> selectByUser(User user);
}
