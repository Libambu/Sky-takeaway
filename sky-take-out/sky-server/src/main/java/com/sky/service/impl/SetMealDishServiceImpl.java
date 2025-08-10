package com.sky.service.impl;

import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.service.SetMealDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetMealDishServiceImpl implements SetMealDishService {
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public void insert(SetmealDish dishList) {
        setmealDishMapper.insert(dishList);
    }
}
