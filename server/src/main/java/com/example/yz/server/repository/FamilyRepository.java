package com.example.yz.server.repository;

import com.example.yz.server.pojo.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Integer> {

    // 根据 adminID 查询对应的 familyID
    // 使用 @Query 注解查询 familyID
    @Query("SELECT f.familyID FROM Family f WHERE f.adminID = :adminID")
    Optional<Integer> findByAdminID(int adminID);

    // 获取所有家庭数据
    List<Family> findAll();

    Optional<Family> findByFamilyID(int FamilyID);
}
