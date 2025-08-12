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
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //首先要先判断当前添加的数据在不在数据库中，若在则update数量+1不在则insert进去
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setDishId(shoppingCartDTO.getDishId());
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCart.setSetmealId(shoppingCartDTO.getSetmealId());
        shoppingCart.setDishFlavor(shoppingCartDTO.getDishFlavor());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list!=null&&list.size()>0){
            ShoppingCart shoppingCart1 = list.get(0);
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            shoppingCartMapper.updateNumber(shoppingCart1);
        }else{
            //如果不存在则插入数据
            //接下来要看要插入的是菜品还是套餐
            Long dishId = shoppingCart.getDishId();
            Long setmealId = shoppingCart.getSetmealId();
            if(dishId != null){
                //菜品
                Dish dish = dishMapper.selectDishById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());

            }else{
                //套餐
                Setmeal setmeal = setmealMapper.selectById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
            }
            shoppingCartMapper.insert(shoppingCart);
        }

    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(currentId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    @Override
    public void deleteCart() {
        shoppingCartMapper.deleteCart(BaseContext.getCurrentId());
    }
}
