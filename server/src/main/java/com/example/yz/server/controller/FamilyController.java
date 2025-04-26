package com.example.yz.server.controller;

import com.example.yz.server.dto.UpdateFamilyMemberDTO;
import com.example.yz.server.pojo.Family;
import com.example.yz.server.pojo.FamilyMember;
import com.example.yz.server.pojo.User;
import com.example.yz.server.repository.FamilyMemberRepository;
import com.example.yz.server.repository.UserRepository;
import com.example.yz.server.service.FamilyMemberService;
import com.example.yz.server.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/family")
@CrossOrigin(origins = "*")
public class FamilyController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private FamilyMemberService familyMemberService;

    //获取家庭成员信息
    @GetMapping("/{familyId}/members")
    public ResponseEntity<List<Map<String, Object>>> getFamilyMembers(@PathVariable int familyId) {
        List<FamilyMember> familyMembers = familyMemberRepository.findByFamilyID(familyId);
        List<Integer> userIds = familyMembers.stream().map(FamilyMember::getUserID).collect(Collectors.toList());
        List<User> users = userRepository.findByUserIdIn(userIds);

        List<Map<String, Object>> result = new ArrayList<>();
        for (FamilyMember fm : familyMembers) {
            for (User user : users) {
                if (Integer.valueOf(fm.getUserID()).equals(user.getUserId())) {
                    Map<String, Object> memberInfo = new HashMap<>();
                    memberInfo.put("id", fm.getUserID());
                    memberInfo.put("name", user.getFullName());
                    memberInfo.put("email", user.getEmail());
                    memberInfo.put("phone", user.getPhoneNumber());
                    memberInfo.put("dateRegistered", user.getDateRegistered());
                    memberInfo.put("relationship", fm.getRelationship());
                    result.add(memberInfo);
                }
            }
        }
        return ResponseEntity.ok(result);
    }

    // 获取单个家庭成员的详细信息
    @GetMapping("/members/{id}")
    public ResponseEntity<Map<String, Object>> getFamilyMemberById(@PathVariable int id) {
        // 获取 FamilyMember 表中的数据
        System.out.println("Fetching family member with id: " + id);
        // 获取 UserID 为 id 的 FamilyMember 数据
        Optional<FamilyMember> familyMemberOpt = familyMemberRepository.findByUserID(id);
        if (!familyMemberOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Family member not found"));
        }

        FamilyMember familyMember = familyMemberOpt.get();

        // 获取 User 表中的数据
        Optional<User> userOpt = userRepository.findById(familyMember.getUserID());
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User not found"));
        }

        User user = userOpt.get();
        System.out.println("Found User: " + user);

        // 构建返回的成员信息
        Map<String, Object> memberInfo = new HashMap<>();
        memberInfo.put("id", user.getUserId());
        memberInfo.put("name", user.getFullName());
        memberInfo.put("email", user.getEmail());
        memberInfo.put("phone", user.getPhoneNumber());
        memberInfo.put("dateRegistered", user.getDateRegistered());
        memberInfo.put("relationship", familyMember.getRelationship());

        return ResponseEntity.ok(memberInfo);
    }

    // 更新家庭成员的信息
    @PutMapping("/members/{id}")
    public ResponseEntity<Map<String, Object>> updateFamilyMember(@PathVariable int id, @RequestBody UpdateFamilyMemberDTO updatedData) {
        // 获取 FamilyMember 表中的数据
        Optional<FamilyMember> familyMemberOpt = familyMemberRepository.findByUserID(id);
        if (!familyMemberOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Family member not found"));
        }

        FamilyMember familyMember = familyMemberOpt.get();

        // 更新 FamilyMember 的 relationship 字段
        familyMember.setRelationship(updatedData.getRelationship());

        // 获取 User 表中的数据
        Optional<User> userOpt = userRepository.findById(familyMember.getUserID());
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User not found"));
        }

        User user = userOpt.get();

        // 更新 User 信息
        if (updatedData.getFullName() != null) {
            user.setFullName(updatedData.getFullName());
        }
        if (updatedData.getEmail() != null) {
            user.setEmail(updatedData.getEmail());
        }
        if (updatedData.getPhoneNumber() != null) {
            user.setPhoneNumber(updatedData.getPhoneNumber());
        }

        // 保存更新后的数据
        userRepository.save(user); // 更新用户信息
        familyMemberRepository.save(familyMember); // 更新家庭成员信息

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Family member updated successfully");
        response.put("updatedMember", familyMember);

        return ResponseEntity.ok(response);
    }

    // 根据 adminID 查询 familyID
    //family表
    @GetMapping("/{adminID}")
    public Optional<Integer> getFamilyIDByAdminID(@PathVariable int adminID) {
        return familyService.findFamilyByAdminId(adminID);
    }

    @GetMapping("/find/{familyID}")
    public Optional<Family> getAdminIDByFamilyID(@PathVariable int familyID) {
        return familyService.findAdminByFamilyId(familyID);
    }


    // 添加家庭成员
    @PostMapping("/{familyID}/addMember")
    public ResponseEntity<String> addMember(
            @PathVariable int familyID,
            @RequestBody Map<String, Object> request) {

        // 从请求体中提取 userId 和 relationship
        int userId = (int) request.get("userId");
        String relationship = (String) request.get("relationship");
        // 检查用户是否已是家庭成员（可选）
        FamilyMember existingMember = familyMemberRepository.findByUserIDAndFamilyID(userId, familyID);
        if (existingMember != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("该用户已是家庭成员");
        }

        // 创建新的家庭成员对象
        FamilyMember newMember = new FamilyMember();
        newMember.setUserID(userId);
        newMember.setFamilyID(familyID);
        newMember.setRelationship(relationship);

        // 保存新成员到数据库
        familyMemberRepository.save(newMember);

        return ResponseEntity.ok("家庭成员添加成功");
    }

    @DeleteMapping("/deleteMember")
    public ResponseEntity<String> deleteMember(@RequestBody Map<String, Object> request) {
        int userId = (int) request.get("userId");

        // 执行删除操作，根据 userId 在 familymembers 表中删除该成员
        boolean success = familyMemberService.deleteMemberByUserId(userId);

        if (success) {
            return ResponseEntity.ok("删除成功");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("删除失败");
        }
    }

    // 获取所有家庭数据的 API
    @GetMapping
    public List<Family> getAllFamilies() {
        return familyService.getAllFamilies();
    }


}

