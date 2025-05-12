package com.yu.service.impl;

import com.yu.Mapper.AdminMapper;
import com.yu.pojo.Admin;
import com.yu.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Override
    public Admin selectByName(String username) {
        return adminMapper.selectByName(username);
    }
}
