package com.example.yz.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// CategoryStatsDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStatsDTO {
    private String category;  // 分类名称
    private BigDecimal amount; // 分类总金额
}
