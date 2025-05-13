package com.example.yz.server.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FinancialRecord {
    private String date;        // 日期
    private String type;        // 类型(收入/支出)
    private BigDecimal amount;  // 金额
    private String category;    // 分类
    private String description; // 描述
}
