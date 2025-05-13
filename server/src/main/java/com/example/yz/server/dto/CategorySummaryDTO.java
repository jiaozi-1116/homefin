package com.example.yz.server.dto;

import java.math.BigDecimal;

public class CategorySummaryDTO {
    private String category;
    private BigDecimal totalAmount;

    // 构造函数、Getter和Setter
    public CategorySummaryDTO() {}

    public CategorySummaryDTO(String category, BigDecimal totalAmount) {
        this.category = category;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
