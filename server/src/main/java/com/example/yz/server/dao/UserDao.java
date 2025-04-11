package com.example.yz.server.dao;

import com.example.yz.server.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserDao {
    @Select("select * from users")
    public List<User> findAll();
}
