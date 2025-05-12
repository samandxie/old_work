package com.yu.Mapper;

import com.yu.pojo.Order;
import com.yu.pojo.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    @Select("select * from order_item where order_id = #{orderId}")
    @ResultMap("BaseResultMap")
    List<OrderItem> selectByOrderId(Integer orderId);

    @Insert("insert into order_item (order_id, book_id, price, quantity) VALUES " +
            "(#{orderId}, #{bookId}, #{price}, #{quantity})")
    void insert(OrderItem orderItem);
}
