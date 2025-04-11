package com.example.yz.server.service;


import com.example.yz.server.pojo.Expense;
import com.example.yz.server.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    // 获取用户的支出记录
    public List<Expense> getExpensesByUserId(Integer userId) {
        return expenseRepository.findByUserId(userId);
    }

    public List<Expense> getExpensesByFamilyId(Integer familyId) {
        return expenseRepository.findByFamilyId(familyId);
    }

    // 添加支出记录
    public Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    // 删除支出记录
    public void deleteExpense(Integer expenseId) {
        expenseRepository.deleteById(expenseId);
    }

    public List<Map<String, Object>> getExpenseData(Integer userId) {
        // 从数据库中查询指定用户的所有支出数据
        List<Expense> expenseList = expenseRepository.findByUserId(userId);

        // 使用 Map 来按 'category' 分组并累加 'amount'
        Map<String, BigDecimal> expenseMap = expenseList.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                ));

        // 将结果转换为前端需要的格式：x轴的值和y轴的值
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : expenseMap.entrySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("category", entry.getKey());
            data.put("amount", entry.getValue());
            result.add(data);
        }

        return result;
    }
}
