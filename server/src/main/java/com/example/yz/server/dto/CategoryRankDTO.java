package com.example.yz.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// CategoryRankDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRankDTO {
    private String category;  // 分类名称
    private Integer count;    // 交易笔数
    private BigDecimal amount; // 总金额
}