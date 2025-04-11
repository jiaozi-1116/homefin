package com.example.yz.server.controller;


import com.example.yz.server.pojo.Expense;
import com.example.yz.server.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expense")
@CrossOrigin(origins = "*")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    // 获取用户的支出记录
    @GetMapping("/{userId}")
    public ResponseEntity<?> getExpenses(@PathVariable Integer userId) {
        try {
            List<Expense> expenses = expenseService.getExpensesByUserId(userId);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("获取支出记录失败: " + e.getMessage());
        }
    }

    // 添加支出
    @PostMapping
    public ResponseEntity<?> addExpense(@RequestBody Expense expense) {
        try {
            expenseService.saveExpense(expense);
            return ResponseEntity.ok("支出记录已添加");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("添加支出失败: " + e.getMessage());
        }
    }

    // 删除支出
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable Integer expenseId) {
        try {
            expenseService.deleteExpense(expenseId);
            return ResponseEntity.ok("支出记录已删除");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("删除支出失败: " + e.getMessage());
        }
    }

    @GetMapping("/all/{userId}")
    public List<Map<String, Object>> getExpenseData(@PathVariable Integer userId) {
        return expenseService.getExpenseData(userId);
    }

    @GetMapping("/allExpense/{userId}")
    public List<Expense> getExpense(@PathVariable Integer userId) {
        return expenseService.getExpensesByUserId(userId);
    }


    // ExpenseController.java
    @GetMapping("/family/{familyId}")
    public List<Expense> getExpensesByFamily(@PathVariable Integer familyId) {
        return expenseService.getExpensesByFamilyId(familyId);
    }

}
