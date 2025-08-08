package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatProperties weChatProperties;

    private String wxUrl = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 实现登录功能，完成返回user信息
     * @param jsCode
     * @return
     */
    @Override
    public User login(String jsCode) {
        //调用微信接口服务
        HashMap<String,String> param = new HashMap<>();
        param.put("appid",weChatProperties.getAppid());
        param.put("secret",weChatProperties.getSecret());
        param.put("js_code",jsCode);
        param.put("grant_type","authorization_code");
        //返回的是一个字符串json格式
        String json = HttpClientUtil.doGet(wxUrl,param);
        //转为json对象
        JSONObject jsonObject = JSONObject.parseObject(json);
        String open_id = jsonObject.getString("openid");
        //判断open_id是否为空，返回登录异常
        if(open_id==null){
            throw  new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //开始判断用户是否是新用户
        User user = userMapper.selectByOpenid(open_id);
        //新用户自动注册
        if(user == null){
            user = new User();
            user.setOpenid(open_id);
            user.setCreateTime(LocalDateTime.now());
            userMapper.insert(user);
        }
        return user;
    }
}
