package com.example.yz.server.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthlyTrendResponse {
    private Integer month;
    private BigDecimal income;         // 本年收入
    private BigDecimal expense;       // 本年支出
    private BigDecimal lastYearIncome; // 去年收入
    private BigDecimal lastYearExpense; // 去年支出

    public MonthlyTrendResponse() {
        this.income = BigDecimal.ZERO;
        this.expense = BigDecimal.ZERO;
        this.lastYearIncome = BigDecimal.ZERO;
        this.lastYearExpense = BigDecimal.ZERO;
    }
}
