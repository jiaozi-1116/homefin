package com.example.yz.server.controller;


import com.example.yz.server.dto.MemberCategorySummary;
import com.example.yz.server.pojo.Expense;
import com.example.yz.server.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
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

    //个人开支
    @GetMapping("/allExpense/{userId}")
    public List<Expense> getExpense(@PathVariable Integer userId) {
        return expenseService.getExpensesByUserId(userId);
    }


    // 家庭开支
    @GetMapping("/family/{familyId}")
    public List<Expense> getExpensesByFamily(@PathVariable Integer familyId) {
        return expenseService.getExpensesByFamilyId(familyId);
    }

    // 分类支出统计（家庭）
    @GetMapping("/category-summary/{familyId}")
    public ResponseEntity<?> getCategorySummary(
            @PathVariable Integer familyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            Map<String, BigDecimal> summary = expenseService.getCategorySummary(
                    familyId,
                    startDate,
                    endDate
            );
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("获取分类统计失败: " + e.getMessage());
        }
    }

    //分类统计（个人）
    @GetMapping("/category-summary/member/{memberId}")
    public ResponseEntity<?> getCategorySummaryUserId(
            @PathVariable Integer memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            Map<String, BigDecimal> summary = expenseService.getCategorySummaryUserId(
                    memberId,
                    startDate,
                    endDate
            );
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("获取分类统计失败: " + e.getMessage());
        }
    }

    //某个家庭的预算详情
    @GetMapping("/member-category-summary/{familyId}")
    public List<com.example.yz.server.entity.Expense> getMemberCategorySummary(
            @PathVariable Integer familyId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String category
    ) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start = sdf.parse(startDate);
        Date end = sdf.parse(endDate);
        return expenseService.getMemberCategorySummary(familyId, start, end, category);
    }

    //个人的预算详情
    @GetMapping("/member-category/{userId}")
    public List<com.example.yz.server.entity.Expense> getMemberCategory(
            @PathVariable Integer userId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String category
    ) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start = sdf.parse(startDate);
        Date end = sdf.parse(endDate);
        return expenseService.getMemberCategory(userId, start, end, category);
    }


}
