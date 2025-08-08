package com.sky.service;
import com.sky.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    User login(String jsCode);
}
