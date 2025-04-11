package com.example.yz.server.controller;

import com.example.yz.server.pojo.Budget;
import com.example.yz.server.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    /**
     * 获取指定用户的预算列表
     * @param userId 用户ID
     * @return 用户相关的预算列表
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<Budget>> getBudgetsByUserId(@PathVariable Integer userId) {
        List<Budget> budgets = budgetService.getBudgetsByUserId(userId);
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/family/{familyId}")
    public ResponseEntity<List<Budget>> getBudgetsByFamilyId(@PathVariable Integer familyId) {
        List<Budget> budgets = budgetService.getBudgetsByFamilyId(familyId);
        return ResponseEntity.ok(budgets);
    }

    /**
     * 添加新预算
     * @param budget 预算对象
     * @return 添加后的预算
     */
    @PostMapping()
    public ResponseEntity<Budget> addBudget(@RequestBody Budget budget) {
        Budget savedBudget = budgetService.addBudget(budget);
        return ResponseEntity.ok(savedBudget);
    }

    /**
     * 更新预算信息
     * @param budgetId 预算ID
     * @param updatedBudget 更新后的预算对象
     * @return 更新后的预算
     */
    @PutMapping("/{budgetId}")
    public ResponseEntity<Budget> updateBudget(
            @PathVariable Integer budgetId,
            @RequestBody Budget updatedBudget
    ) {
        Budget budget = budgetService.updateBudget(budgetId, updatedBudget);
        return ResponseEntity.ok(budget);
    }

    /**
     * 删除预算
     * @param budgetId 预算ID
     * @return 响应状态
     */
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget( @PathVariable Integer budgetId) {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }
}
