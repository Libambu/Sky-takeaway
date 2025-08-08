package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    public void saveDish(DishDTO dishDTO);
    PageResult pageSelect(DishPageQueryDTO dto);

    void deleteDish(List<Long> ids);

    DishVO selectAll(Long id);

    void updataDish(DishDTO dishDTO);
}
