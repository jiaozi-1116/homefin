package com.example.yz.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@TableName("financialreports")
public class FinancialReport {
    @TableId(type = IdType.AUTO)
    @TableField("ReportID")
    private Integer reportId;

    @TableField(value = "FamilyID", exist = true)
    private Integer familyId;

    @TableField(value = "report_type", exist = true)
    private String reportType;

    private Integer year;

    private Integer month;

    @TableField(value = "generated_date", exist = true)
    private LocalDate generatedDate;

    @TableField(value = "financial_health_index", exist = true)
    private BigDecimal financialHealthIndex;
}

