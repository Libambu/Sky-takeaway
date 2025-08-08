package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    void deleteFlavorById(List<Long> ids);

    List<DishFlavor> selectDishflavorByDishId(Long id);

    void insert(DishFlavor dishFlavor);
}
