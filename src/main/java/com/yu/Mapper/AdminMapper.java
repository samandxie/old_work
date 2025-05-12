package com.yu.Mapper;

import com.yu.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {

    @Select("select * from admin where username=#{username}")
    Admin selectByName(String username);
}
