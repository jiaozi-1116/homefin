package com.example.yz.server.pojo;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BudgetID")
    private Integer budgetId;

    @Column(name = "UserID", nullable = true)
    private Integer userId;

    @Column(name = "FamilyID", nullable = true)
    private Integer familyId;

    @Column(name = "Amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;

    @Column(name = "Category", length = 50, nullable = true)
    private String category;
}
