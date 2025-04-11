package com.example.yz.server.service;

import com.example.yz.server.pojo.Family;
import com.example.yz.server.repository.FamilyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FamilyService {

    @Autowired
    private FamilyRepository familyRepository;

    // 根据 adminID 查找 FamilyID
    //family
    public Optional<Integer> findFamilyByAdminId(int adminId) {
        return familyRepository.findByAdminID(adminId);
    }

    public Optional<Family> findAdminByFamilyId(int familyID) {
        return familyRepository.findByFamilyID(familyID);
    }

    // 获取所有家庭数据
    public List<Family> getAllFamilies() {
        return familyRepository.findAll();
    }

    public Optional<Family> getFamilyById(Integer familyID) {
        return familyRepository.findById(familyID);
    }
}

