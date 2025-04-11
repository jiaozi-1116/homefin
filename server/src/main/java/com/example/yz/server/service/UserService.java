package com.example.yz.server.service;


import com.example.yz.server.pojo.User;
import com.example.yz.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // 获取所有用户
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    public User updateUser(Integer userId, User user) {
        Optional<User> existingUserOpt = userRepository.findById(userId);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            existingUser.setUsername(user.getUsername());
            existingUser.setPassword(user.getPassword());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhoneNumber(user.getPhoneNumber()); // 更新其他字段
            existingUser.setFullName(user.getFullName());
            return userRepository.save(existingUser);
        } else {
            System.out.println("User not found with ID: " + userId);
            return null;
        }
    }
}
