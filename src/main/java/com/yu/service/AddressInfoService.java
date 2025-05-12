package com.yu.service;

import com.yu.pojo.AddressInfo;

import java.util.List;

public interface AddressInfoService {
    List<AddressInfo> selectByUserId(Integer userId);

    void insert(AddressInfo addressInfo);

    int update(AddressInfo addressInfo);

    int delete(Integer id);

    int setDefault(Integer id, Integer userId);
}
