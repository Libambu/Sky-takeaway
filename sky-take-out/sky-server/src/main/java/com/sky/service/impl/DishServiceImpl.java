package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    public void saveDish(DishDTO dishDTO) {
        LocalDateTime creatTime = LocalDateTime.now();
        LocalDateTime updateTime = LocalDateTime.now();
        Dish dish = new Dish(null,dishDTO.getName(),dishDTO.getCategoryId(),dishDTO.getPrice(),dishDTO.getImage(),dishDTO.getDescription(),dishDTO.getStatus(),creatTime,updateTime, BaseContext.getCurrentId(),BaseContext.getCurrentId());
        List<DishFlavor> dishFlavorslist = dishDTO.getFlavors();
        dishMapper.insertDish(dish);
        Long dishId = dish.getId();
        for(DishFlavor dishFlavor : dishFlavorslist){
            dishFlavor.setDishId(dishId);
            dishMapper.insertDishFlavor(dishFlavor);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageResult pageSelect(DishPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(),dto.getPageSize());
        Page<DishVO> page = dishMapper.pageSelect(dto);
        List<DishVO> dishs = page.getResult();
        Long total = page.getTotal();
        return new PageResult(total,dishs);
    }

    @Override
    public void deleteDish(List<Long> ids) {
        //如果这批菜品中存在正在售卖的则无法删除
        for(Long id : ids){
            Dish dish = dishMapper.selectDishById(id);
            if(dish.getStatus() == 1){
                //当前菜品正在起售中无法删除
                throw  new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //在判断是否被套餐关联
        List<Long> idList = setmealDishMapper.idList(ids);
        if(idList != null && idList.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        //开始删除菜品表的菜品数据，口味也要删除
        dishMapper.deleteById(ids);
        dishFlavorMapper.deleteFlavorById(ids);
    }

    @Override
    public DishVO selectAll(Long id) {
        Dish d = dishMapper.selectDishById(id);
        List<DishFlavor>flavorsList = dishFlavorMapper.selectDishflavorByDishId(id);
        return new DishVO(d.getId(),d.getName(),d.getCategoryId(),d.getPrice(),d.getImage(),d.getDescription(),d.getStatus(),d.getUpdateTime(),null,flavorsList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updataDish(DishDTO d) {
        LocalDateTime updateTime = LocalDateTime.now();
        Dish dish = new Dish(d.getId(),d.getName(),d.getCategoryId(),d.getPrice(),d.getImage(),d.getDescription(),d.getStatus(),null,updateTime,null,BaseContext.getCurrentId());
        List<DishFlavor> dishFlavors =  d.getFlavors();
        //先将原来菜品的口味全部删除
        List<Long> id = new ArrayList<>();
        id.add(d.getId());
        dishFlavorMapper.deleteFlavorById(id);
        //然后重新插入口味味
        for(DishFlavor dishFlavor : dishFlavors){
            dishFlavorMapper.insert(dishFlavor);
        }
        //更新菜品
        dishMapper.updataDish(dish);
    }

}
