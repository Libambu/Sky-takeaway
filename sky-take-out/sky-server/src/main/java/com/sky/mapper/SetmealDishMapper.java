package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    List<Long> idList(List<Long>ids);

    void insert(SetmealDish dishList);

    void deteleBySetmealId(List<Long> ids);
}
