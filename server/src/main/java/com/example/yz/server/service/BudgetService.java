package com.example.yz.server.service;

import com.example.yz.server.pojo.Budget;
import com.example.yz.server.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    public List<Budget> getBudgetsByUserId(Integer userId) {
        return budgetRepository.findByUserId(userId);
    }

    public List<Budget> getBudgetsByFamilyId(Integer familyId) {
        return budgetRepository.findByFamilyId(familyId);
    }

    public List<Budget> getBudgetsByCategory(String category) {
        return budgetRepository.findByCategory(category);
    }

    public List<Budget> getBudgetsInAmountRange(Double minAmount, Double maxAmount) {
        return budgetRepository.findByAmountBetween(minAmount, maxAmount);
    }

    public List<Budget> getBudgetsByDateRange(LocalDate startDate, LocalDate endDate) {
        return budgetRepository.findByStartDateBetween(startDate, endDate);
    }

    // 添加预算
    public Budget addBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    // 更新预算
    public Budget updateBudget(Integer budgetId, Budget updatedBudget) {
        Budget existingBudget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        existingBudget.setAmount(updatedBudget.getAmount());
        existingBudget.setStartDate(updatedBudget.getStartDate());
        existingBudget.setEndDate(updatedBudget.getEndDate());
        existingBudget.setCategory(updatedBudget.getCategory());
        return budgetRepository.save(existingBudget);
    }

    // 删除预算
    public void deleteBudget(Integer budgetId) {
        budgetRepository.deleteById(budgetId);
    }
}