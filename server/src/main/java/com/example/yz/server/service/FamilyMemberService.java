package com.example.yz.server.service;

import com.example.yz.server.pojo.Family;
import com.example.yz.server.pojo.FamilyMember;
import com.example.yz.server.pojo.User;
import com.example.yz.server.repository.FamilyMemberRepository;
import com.example.yz.server.repository.FamilyRepository;
import com.example.yz.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FamilyMemberService {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    public boolean deleteMemberByUserId(int userId) {
        // 查找 familymembers 表中对应的记录并删除
        Optional<FamilyMember> familyMember = familyMemberRepository.findByUserID(userId);

        if (familyMember.isPresent()) {
            // 执行删除操作
            familyMemberRepository.delete(familyMember.get());
            return true;
        } else {
            // 没有找到该成员
            return false;
        }
    }

    //根据userID查找FamilyID
    public Optional<Integer> findFamilyIdByUserId(int userId) {
        return familyMemberRepository.findFamilyIdByUserID(userId);
    }


}
