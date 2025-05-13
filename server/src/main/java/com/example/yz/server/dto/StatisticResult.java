package com.example.yz.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// StatisticResult.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticResult {
    private String date;       // 分组日期（格式：yyyy-MM-dd 或 yyyy-MM）
    private BigDecimal amount; // 该日期的总金额
}
