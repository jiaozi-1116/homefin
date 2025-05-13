package com.example.yz.server.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FinancialSummary {
    private String date; // 日期或月份
    private BigDecimal expense; // 支出总额
    private BigDecimal income;  // 收入总额
}
