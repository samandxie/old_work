package com.yu.service.impl;

import com.yu.Mapper.ShoppingCartMapper;
import com.yu.exception.CustomizeException;
import com.yu.pojo.ShoppingCart;
import com.yu.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Override
    public void addToShoppingCart(ShoppingCart cart) {
        ShoppingCart record = shoppingCartMapper.selectByUserIdAndBookId(cart.getUserId(), cart.getBookId());
        if (record != null) {
            //如果购物车存在该商品的
            throw new CustomizeException("您已经添加过该商品了,不能重复添加哦");
        }
        shoppingCartMapper.insert(cart);
    }

    @Override
    public List<ShoppingCart> getShoppingCartByUserId(Integer userId) {
        return shoppingCartMapper.selectByUserId(userId);
    }

    @Override
    public void deleteByCartId(Integer cartId) {
        shoppingCartMapper.deleteByCartId(cartId);
    }

    @Override
    public void updateCartByCartId(Integer cartId, Integer quantity) {
        shoppingCartMapper.updateCartByCartId(cartId, quantity);
    }

    @Override
    public void deleteCartItems(int[] cartIds) {
        for (Integer cartId:cartIds) {
            deleteShoppingCartByCartId(cartId);
        }
    }

    private void deleteShoppingCartByCartId(Integer cartId) {
        shoppingCartMapper.deleteByCartId(cartId);
    }

}
