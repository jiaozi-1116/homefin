package com.example.yz.server.pojo;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "financialreports")
public class FinancialReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReportID")
    private Integer reportId;

    @Column(name = "FamilyID", nullable = false)
    private Integer familyId;

//    @Column(name = "UserID", nullable = false)
//    private Integer userId;

    @Column(name = "report_type", length = 50, nullable = false)
    private String reportType;

    private Integer year;

    private Integer month;

    @Column(name = "generated_date", nullable = false)
    private LocalDate generatedDate;

    @Column(name = "financial_health_index")
    private BigDecimal financialHealthIndex;
}

