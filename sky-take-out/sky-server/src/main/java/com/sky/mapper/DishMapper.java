package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    int insertDish(Dish dish);

    int insertDishFlavor(DishFlavor dishFlavor);

    Page<DishVO> pageSelect(DishPageQueryDTO dto);

    @Select("select * from dish where id = #{id}")
    Dish selectDishById(Long id);

    void deleteById(List<Long> ids);

    void updataDish(Dish dish);
}
