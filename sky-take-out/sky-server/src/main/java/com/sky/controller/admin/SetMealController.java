package com.sky.controller.admin;

import com.sky.context.BaseContext;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealDishService;
import com.sky.service.SetMealService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api("套餐相关接口")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private SetMealDishService setMealDishService;

    @PostMapping()
    @ApiOperation("新增套餐")
    @Transactional(rollbackFor = Exception.class)
    public Result saveMeal(@RequestBody SetmealDTO setmealDTO){
        SetmealDTO s = setmealDTO;
        Setmeal setmeal = new Setmeal(s.getId(),s.getCategoryId(),s.getName(),s.getPrice(),s.getStatus(),s.getDescription(),s.getImage(), LocalDateTime.now(),LocalDateTime.now(), BaseContext.getCurrentId(),BaseContext.getCurrentId());
        setMealService.insertMeal(setmeal);
        List<SetmealDish> dishLists = s.getSetmealDishes();
        for(SetmealDish dishList : dishLists){
            dishList.setSetmealId(setmeal.getId());
            setMealDishService.insert(dishList);
        }
        return Result.success();
    }
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result pageSetmeal(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult = setMealService.pageSelect(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping()
    @ApiOperation("删除套餐")
    public Result deleteMeal(@RequestParam List<Long>ids){
        setMealService.deleteMeal(ids);
        return Result.success();
    }
    /**
     * 套餐起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        setMealService.startOrStop(status, id);
        return Result.success();
    }

}
