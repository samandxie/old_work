package com.yu.Mapper;

import com.yu.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {


    int insert(User user);

    @Select("select * from user where user_name = #{username}")
    User findByName(String username);

    @Update("update user set user_name = #{userName},email = #{email} where user_id = #{userId}")
    void updateByUserId(User user);
    @Update("update user set user_id = #{userId},email = #{email} where user_name = #{userName}")
    void updateByUser(User user);

    @Update("update user set user_pic = #{avatarUrl} where user_id = #{id}")
    int updateAvatar(String avatarUrl,  Integer id);

    @Update("update user set password = #{newPwd} where user_id = #{id}")
    void updatePwd(String newPwd, Integer id);

    @Select("select * from user")
    List<User> selectAllUser();

    @Update("update user set user_name = #{userName},email = #{email},password = #{password} where user_id = #{userId}")
    void updateByAdmin(User user);

    @Delete("delete from user where user_id = #{id}")
    void deleteById(Integer id);

    List<User> selectByUser(User user);

    @Select("select * from user where user_id = #{userId}")
    List<User> selectByUserId(Integer userId);
}
