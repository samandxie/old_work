package com.yu.service.impl;

import com.yu.Mapper.UserMapper;
import com.yu.pojo.Result;
import com.yu.pojo.User;
import com.yu.service.UserService;
import com.yu.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    public User findByName(String username) {
        return userMapper.findByName(username);
    }

    public void register(User user) {
        user.setJoinTime(new Date());
        user.setUserPic("src/main/resources/img/defaultUserPicImg.webp");
        userMapper.insert(user);
    }

    @Override
    public void updateByUser(User user) {
        userMapper.updateByUser(user);
    }

    @Override
    public void updateByUserId(User user) {
        userMapper.updateByUserId(user);
    }

    @Override
    public int updateAvatar(String avatarUrl, Integer userId) {
        return userMapper.updateAvatar(avatarUrl, userId);
    }

    @Override
    public void updatePwd(String newPwd, Integer id) {
        userMapper.updatePwd(newPwd, id);
    }

    @Override
    public List<User> selectAllUser() {
        return userMapper.selectAllUser();
    }

    @Override
    public void updateByAdmin(User user) {
        userMapper.updateByAdmin(user);
    }

    @Override
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    @Override
    public List<User> selectByUser(User user) {
        if (user.getUserName().isEmpty())
            user.setUserName(null);
        if (user.getEmail().isEmpty())
            user.setEmail(null);
        return userMapper.selectByUser(user);
    }
}
