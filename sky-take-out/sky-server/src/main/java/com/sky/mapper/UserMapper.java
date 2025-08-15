package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    User selectByOpenid(String openId);

    void insert(User user);

    //动态条件统计用户数量
    Integer getUserNumByDate(Map map);

    Integer countByMap(Map map);
}
