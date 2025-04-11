package com.example.yz.server.controller;

// src/main/java/com/example/demo/controller/LoginController.java

import com.example.yz.server.pojo.User;
import com.example.yz.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");

        System.out.println("Received login request: " + username);

        User user = (User) userRepository.findByUsername(username);

        Map<String, Object> response = new HashMap<>();
        if (user != null && user.getPassword().equals(password)) {
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("role", user.getRole()); // 返回用户角色
            response.put("name", user.getUsername()); // 返回用户名
            response.put("id",user.getUserId());//返回id
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
        }

        return response;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.findByUsername(user.getUsername()) != null) {
            response.put("success", false);
            response.put("message", "用户名已存在");
        } else {
//            user.setDateRegistered(new Date()); // 设置当前日期和时间
            Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
            user.setDateRegistered(currentDate);
            userRepository.save(user);
            response.put("success", true);
            response.put("message", "注册成功");
        }

        return response;
    }
}

