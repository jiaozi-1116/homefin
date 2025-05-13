package com.example.yz.server.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberCategorySummary {
    private Integer userID;
    private String memberName; // 假设从用户表获取姓名，这里简单用 userID 代替
    private BigDecimal spentAmount;
}
