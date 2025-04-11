package com.example.yz.server.dto;

import com.example.yz.server.pojo.Expense;
import com.example.yz.server.pojo.Income;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReportRequest {
    private String reportType;
    private Integer year;
    private Integer month;
    // getters/setters
}

