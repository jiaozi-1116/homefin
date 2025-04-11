package com.example.yz.server.pojo;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

import java.util.Date;

@Data
@Entity
@Table(name = "incomes")
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IncomeID")
    private Integer incomeId; // 收入ID

    @Column(name = "UserID")
    private Integer userId; // 用户ID

    @Column(name = "FamilyID")
    private Integer familyId; // 家庭ID

    @Column(name = "Amount", precision = 10, scale = 2)
    private BigDecimal amount; // 收入金额

    @Column(name = "Source", length = 100)
    private String source; // 收入来源

    @Column(name = "Date")
    private Date date; // 收入日期

    @Column(name = "Description")
    private String description; // 描述

}
