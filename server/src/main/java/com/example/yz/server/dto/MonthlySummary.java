package com.example.yz.server.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthlySummary {
    private Integer month;
    private BigDecimal total;
}
