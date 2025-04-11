package com.example.yz.server.pojo;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

import java.util.Date;

@Data
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ExpenseID")
    private Integer expenseId; // 支出ID

    @Column(name = "UserID")
    private Integer userId; // 用户ID

    @Column(name = "FamilyID")
    private Integer familyId; // 家庭ID

    @Column(name = "Amount", precision = 10, scale = 2)
    private BigDecimal amount; // 支出金额

    @Column(name = "Category", length = 50)
    private String category; // 支出类别

    @Column(name = "Date")
    private Date date; // 支出日期

    @Column(name = "Description")
    private String description; // 描述

}
