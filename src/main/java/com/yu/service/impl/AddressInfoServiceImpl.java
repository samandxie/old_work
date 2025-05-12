package com.yu.service.impl;

import com.yu.Mapper.AddressInfoMapper;
import com.yu.pojo.AddressInfo;
import com.yu.service.AddressInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressInfoServiceImpl implements AddressInfoService {

    @Autowired
    private AddressInfoMapper addressInfoMapper;

    @Override
    public List<AddressInfo> selectByUserId(Integer userId) {
        return addressInfoMapper.selectByUserId(userId);
    }

    @Override
    public void insert(AddressInfo addressInfo) {
        addressInfoMapper.insert(addressInfo);
    }

    @Override
    public int update(AddressInfo addressInfo) {
        return addressInfoMapper.update(addressInfo);
    }

    @Override
    public int delete(Integer id) {
        return addressInfoMapper.deleteById(id);
    }

    @Override
    public int setDefault(Integer id, Integer userId) {
        addressInfoMapper.setDefaultFalse(id, userId);
        return addressInfoMapper.setDefault(id, userId);
    }
}
