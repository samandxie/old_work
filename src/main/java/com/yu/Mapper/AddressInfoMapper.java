package com.yu.Mapper;

import com.yu.pojo.AddressInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressInfoMapper {

    @Select("select * from address where user_id=#{userId}")
    List<AddressInfo> selectByUserId(Integer userId);

    @Select("select * from address where id=#{addressId}")
    AddressInfo selectByAddressId(Integer addressId);

    @Insert("insert into address (user_id, recipient, phone, province, city, district, address, zipcode, is_default) " +
            "values (#{userId},#{recipient},#{phone},#{province},#{city},#{district},#{address},#{zipcode},#{isDefault})")
    void insert(AddressInfo addressInfo);

    @Update("update address set user_id = #{userId},recipient=#{recipient},phone=#{phone},province=#{province},city=#{city}," +
            "address=#{address},district=#{district},zipcode=#{zipcode},is_default=#{isDefault} " +
            "where id = #{id}")
    int update(AddressInfo addressInfo);

    @Delete("delete from address where id = #{id}")
    int deleteById(Integer id);

    @Update("update address set is_default = 1 where user_id = #{userId} and id = #{id}")
    int setDefault(Integer id, Integer userId);

    @Update("update address set is_default = 0 where user_id = #{userId}")
    int setDefaultFalse(Integer id, Integer userId);
}
