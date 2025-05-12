package com.yu.Mapper;

import com.yu.pojo.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {
    @Select("select * from orders")
    @ResultMap("BaseResultMap")
    List<Order> getAllOrders();

    @Delete("delete from orders where order_id = #{orderId}")
    int deleteByOrderId(Integer orderId);

    @Insert("insert into orders " +
            "(user_id, consignee_name, address, zip, phone_number, " +
            "status, create_time, address_id)values " +
            "(#{userId},#{consigneeName}," +
            "#{address},#{zip},#{phoneNumber},#{status},#{createTime},#{addressId})")
    @Options(useGeneratedKeys = true, keyProperty = "orderId")
    int insert(Order order);

    @Select("select * from orders where order_id = #{orderId}")
    @ResultMap("BaseResultMap")
    Order selectByOrderId(Integer orderId);

    @Update("update orders set user_id = #{userId},consignee_name = #{consigneeName}," +
            "address = #{address},zip = #{zip},phone_number = #{phoneNumber}," +
            "status = #{status},create_time = #{createTime} where order_id = #{orderId}")
    int updateByOrderId(Order record);

    @Select("select * from orders where user_id = #{userId}")
    @ResultMap("BaseResultMap")
    List<Order> selectByUserId(Integer userId);

    List<Order> searchOrders(Order order);

    @Update("update orders set status = #{status} where order_id = #{orderId}")
    int update(Order order);

    @Update("update orders set status = 1 where order_id = #{orderId}")
    int updateStatusByOrderId(Integer orderId);
}
