package com.yu.controller;

import com.yu.pojo.Result;
import com.yu.pojo.ShoppingCart;
import com.yu.pojo.User;
import com.yu.service.ShoppingCartService;
import jakarta.servlet.http.HttpSession;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/list")
    public Result addToShoppingCart(@Validated ShoppingCart cart, HttpSession session) {
        User user = (User) session.getAttribute("user");
        cart.setUserId(user.getUserId());
        shoppingCartService.addToShoppingCart(cart);
        return Result.success();
    }

    @GetMapping("/list")
    public Result getCartByUserId(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return Result.success(shoppingCartService.getShoppingCartByUserId(user.getUserId()));
    }

    @DeleteMapping("/list/{cartId}")
    public Result deleteCartByCartId(@PathVariable Integer cartId) {
        shoppingCartService.deleteByCartId(cartId);
        return Result.success();
    }

    @PutMapping("/list/{cartId}")
    public Result updateCart(@PathVariable Integer cartId, Integer quantity) {
        shoppingCartService.updateCartByCartId(cartId, quantity);
        return Result.success();
    }

    @DeleteMapping("/list")
    public Result deleteCartItems(@RequestBody int[] cartIds) {
        shoppingCartService.deleteCartItems(cartIds);
        return Result.success();
    }
}
