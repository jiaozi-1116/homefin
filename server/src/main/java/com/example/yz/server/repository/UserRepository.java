package com.example.yz.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.yz.server.pojo.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    List<User> findByUserIdIn(List<Integer> ids);

}
