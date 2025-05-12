package com.yu.service;

import com.yu.pojo.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void addToShoppingCart(ShoppingCart cart);

    List<ShoppingCart> getShoppingCartByUserId(Integer userId);

    void deleteByCartId(Integer cartId);

    void updateCartByCartId(Integer cartId, Integer quantity);

    void deleteCartItems(int[] cartIds);
}
