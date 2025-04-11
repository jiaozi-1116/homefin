package com.example.yz.server.repository;

import com.example.yz.server.pojo.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Integer> {
    List<FamilyMember> findByFamilyID(int familyID);
    Optional<FamilyMember> findByUserID(int userID);  // 查询单个 FamilyMember
    // 根据 userId 和 familyId 查找家庭成员
    FamilyMember findByUserIDAndFamilyID(int userID, int familyID);


    // 根据 userID 查询对应的 familyID
    // 使用 @Query 注解查询 familyID
    @Query("SELECT fm.familyID FROM FamilyMember fm WHERE fm.userID = :userID")
    Optional<Integer> findFamilyIdByUserID(int userID);
}

