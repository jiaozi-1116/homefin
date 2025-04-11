package com.example.yz.server.controller;

import com.example.yz.server.service.FamilyMemberService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/member")
@CrossOrigin(origins = "*")
public class FamilyMemberController {
    @Autowired
    private FamilyMemberService familyMemberService;

    @GetMapping("/findFamilyId/{userId}")
    public Optional<Integer> findFamilyId(@PathVariable int userId) {
        return familyMemberService.findFamilyIdByUserId(userId);
    }

}
