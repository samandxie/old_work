package com.yu.Mapper;

import com.yu.pojo.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    @Delete("delete from shopping_cart where user_id = #{userId} and book_id = #{bookId}")
    void deleteByUserIdAndBookId(Integer userId, Integer bookId);

    @Insert("insert into shopping_cart (user_id, book_id, price, quantity) values (#{userId}, #{bookId}, #{price}, #{quantity})")
    void insert(ShoppingCart cart);

    @Select("select * from shopping_cart where user_id = #{userId} and book_id = #{bookId}")
    ShoppingCart selectByUserIdAndBookId(Integer userId, Integer bookId);

    @Select("select * from shopping_cart where user_id = #{userId}")
    @ResultMap("BaseResultMap")
    List<ShoppingCart> selectByUserId(Integer userId);

    @Delete("delete from shopping_cart where cart_id = #{cartId}")
    void deleteByCartId(Integer cartId);

    @Update("update shopping_cart set quantity = #{quantity} where cart_id = #{cartId}")
    void updateCartByCartId(Integer cartId, Integer quantity);
}
