package com.example.yz.server.controller;


import com.example.yz.server.pojo.Income;
import com.example.yz.server.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/income")
@CrossOrigin(origins = "*")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    // 获取用户的收入记录
    @GetMapping("/{userId}")
    public ResponseEntity<?> getIncomes(@PathVariable Integer userId) {
        try {
            List<Income> incomes = incomeService.getIncomesByUserId(userId);
            return ResponseEntity.ok(incomes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("获取收入记录失败: " + e.getMessage());
        }
    }

    // 添加收入
    @PostMapping
    public ResponseEntity<?> addIncome(@RequestBody Income income) {
        try {
            incomeService.saveIncome(income);
            return ResponseEntity.ok("收入记录已添加");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("添加收入失败: " + e.getMessage());
        }
    }

    // 删除收入
    @DeleteMapping("/{incomeId}")
    public ResponseEntity<?> deleteIncome(@PathVariable Integer incomeId) {
        try {
            incomeService.deleteIncome(incomeId);
            return ResponseEntity.ok("收入记录已删除");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("删除收入失败: " + e.getMessage());
        }
    }

    @GetMapping("/all/{userId}")
    public List<Map<String, Object>> getIncomeData(@PathVariable Integer userId) {
        return incomeService.getIncomeData(userId);
    }

    @GetMapping("/allIncome/{userId}")
    public List<Income> getIncome(@PathVariable Integer userId) {
        return incomeService.getIncomesByUserId(userId);
    }

    // IncomeController.java
    @GetMapping("/family/{familyId}")
    public List<Income> getIncomesByFamily(@PathVariable Integer familyId) {
        return incomeService.getIncomesByFamilyId(familyId);
    }

}
