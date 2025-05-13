package com.example.yz.server.service;

import com.example.yz.server.dto.MonthlySummary;
import com.example.yz.server.dto.MonthlyTrendResponse;
import com.example.yz.server.mapper.ExpenseMapper;
import com.example.yz.server.mapper.IncomeMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// FinancialTrendService.java
@Service
@RequiredArgsConstructor
public class FinancialTrendService {

    private final IncomeMapper incomeMapper;
    private final ExpenseMapper expenseMapper;

    public List<MonthlyTrendResponse> getFinancialTrend(Integer familyId, Integer year) {
        // 获取当前年数据
        List<MonthlySummary> currentYearIncome = incomeMapper.getMonthlyIncomeSummary(familyId, year);
        List<MonthlySummary> currentYearExpense = expenseMapper.getMonthlyExpenseSummary(familyId, year);

        // 获取去年数据
        List<MonthlySummary> lastYearIncome = incomeMapper.getMonthlyIncomeSummary(familyId, year - 1);
        List<MonthlySummary> lastYearExpense = expenseMapper.getMonthlyExpenseSummary(familyId, year - 1);

        // 构建12个月的数据结构
        List<MonthlyTrendResponse> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            MonthlyTrendResponse response = new MonthlyTrendResponse();
            response.setMonth(month);

            // 设置本年收入
            int finalMonth = month;
            currentYearIncome.stream()
                    .filter(item -> item.getMonth() == finalMonth)
                    .findFirst()
                    .ifPresent(item -> response.setIncome(item.getTotal()));

            // 设置本年支出
            int finalMonth1 = month;
            currentYearExpense.stream()
                    .filter(item -> item.getMonth() == finalMonth1)
                    .findFirst()
                    .ifPresent(item -> response.setExpense(item.getTotal()));

            // 设置去年收入
            int finalMonth2 = month;
            lastYearIncome.stream()
                    .filter(item -> item.getMonth() == finalMonth2)
                    .findFirst()
                    .ifPresent(item -> response.setLastYearIncome(item.getTotal()));

            // 设置去年支出
            int finalMonth3 = month;
            lastYearExpense.stream()
                    .filter(item -> item.getMonth() == finalMonth3)
                    .findFirst()
                    .ifPresent(item -> response.setLastYearExpense(item.getTotal()));

            result.add(response);
        }

        return result;
    }
}


