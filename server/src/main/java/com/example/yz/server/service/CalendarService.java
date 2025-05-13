package com.example.yz.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.yz.server.dto.FinancialRecord;
import com.example.yz.server.dto.FinancialSummary;
import com.example.yz.server.mapper.ExpenseMapper;
import com.example.yz.server.mapper.FinancialMapper;
import com.example.yz.server.mapper.IncomeMapper;
import com.example.yz.server.pojo.Expense;
import com.example.yz.server.pojo.Income;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// CalendarService.java
@Service
@RequiredArgsConstructor
public class CalendarService {

    private final FinancialMapper financialMapper;

    public Map<String, Object> getPersonalFinancialData(Integer userId, Integer year) {
        Map<String, Object> result = new HashMap<>();

        // 每日汇总
        List<FinancialSummary> daily = financialMapper.getPersonalDailySummary(userId, year);
        Map<String, FinancialSummary> dailyMap = daily.stream()
                .collect(Collectors.toMap(FinancialSummary::getDate, Function.identity()));

        // 每月汇总
        List<FinancialSummary> monthly = financialMapper.getPersonalMonthlySummary(userId, year);
        Map<String, FinancialSummary> monthlyMap = monthly.stream()
                .collect(Collectors.toMap(FinancialSummary::getDate, Function.identity()));

        result.put("daily", dailyMap);
        result.put("monthly", monthlyMap);
        return result;
    }

    public Map<String, Object> getFamilyFinancialData(Integer userId, Integer year) {
        Map<String, Object> result = new HashMap<>();

        // 每日汇总
        List<FinancialSummary> daily = financialMapper.getFamilyDailySummary(userId, year);
        Map<String, FinancialSummary> dailyMap = daily.stream()
                .collect(Collectors.toMap(FinancialSummary::getDate, Function.identity()));

        // 每月汇总
        List<FinancialSummary> monthly = financialMapper.getFamilyMonthlySummary(userId, year);
        Map<String, FinancialSummary> monthlyMap = monthly.stream()
                .collect(Collectors.toMap(FinancialSummary::getDate, Function.identity()));

        result.put("daily", dailyMap);
        result.put("monthly", monthlyMap);
        return result;
    }

    public List<FinancialRecord> getPersonalRecords(Integer userId, String date) {
        return financialMapper.getPersonalRecords(userId, date);
    }

    public List<FinancialRecord> getFamilyRecords(Integer userId, String date) {
        return financialMapper.getFamilyRecords(userId, date);
    }
}
