package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart cart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, cart);
        Long userId = BaseContext.getCurrentId();
        cart.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapper.list(cart);

        if (list != null && list.size() > 0) {
            ShoppingCart shoppingCart = list.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(shoppingCart);
        } else {
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                Dish dish = dishMapper.getById(dishId);
                cart.setName(dish.getName());
                cart.setImage(dish.getImage());
                cart.setAmount(dish.getPrice());
            } else {
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getById(setmealId);
                cart.setName(setmeal.getName());
                cart.setImage(setmeal.getImage());
                cart.setAmount(setmeal.getPrice());
            }

            cart.setNumber(1);
            cart.setCreateTime(LocalDateTime.now());

            shoppingCartMapper.insert(cart);
        }
    }

    /**
     * 查询购物车
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart cart = ShoppingCart.builder()
                .userId(userId)
                .build();
        return shoppingCartMapper.list(cart);
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }
}
