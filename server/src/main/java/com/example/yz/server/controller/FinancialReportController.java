package com.example.yz.server.controller;

import com.example.yz.server.dto.DateFilter;
import com.example.yz.server.dto.ReportRequest;
import com.example.yz.server.pojo.FinancialReport;
import com.example.yz.server.service.FinancialReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class FinancialReportController {

    @Autowired
    private FinancialReportService reportService;

    // 获取家庭财务报告
    @GetMapping("/family/{familyId}")
    public List<FinancialReport> getReportsByFamily(@PathVariable Integer familyId,
                                                    @RequestParam String reportType) {
        return reportService.getReportsByFamilyId(familyId, reportType);
    }

    // 生成财务报告
//    @PostMapping("/generate")
//    public ResponseEntity<FinancialReport> generateReport(@RequestBody ReportRequest reportRequest) {
//        FinancialReport report = reportService.generateReport(
//                reportRequest.getFamilyId(),
//                reportRequest.getUserId(),
//                reportRequest.getReportType(),
//                reportRequest.getData()
//        );
//        return ResponseEntity.status(HttpStatus.CREATED).body(report);
//    }

    // FinancialReportController.java 新增方法
    @PostMapping("/generate/{familyId}")
    public ResponseEntity<?> generateFamilyReport(
            @PathVariable Integer familyId,
            @RequestBody ReportRequest reportRequest) {
        try {
            FinancialReport report = reportService.generateFamilyReport(
                    familyId,
                    reportRequest.getReportType(),
                    reportRequest.getYear(),
                    reportRequest.getMonth()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(report);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "报告生成失败"));
        }
    }

    @GetMapping("/family-generated/{familyId}")
    public ResponseEntity<?> getGeneratedReports(@PathVariable Integer familyId) {
        try {
            List<FinancialReport> reports = reportService.getReportsByFamily(familyId);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("获取报告失败: " + e.getMessage());
        }
    }

    /**
     * 获取收入数据
     *
     * @param dateFilter 日期过滤条件，包含年份和月份
     * @return 收入汇总数据
     */
    @PostMapping("/incomes")
    public Map<String, Object> getIncomes(@RequestBody DateFilter dateFilter) {
        // 获取按来源分组的收入数据
        Map<String, BigDecimal> incomeSummary = reportService.getIncomeData(dateFilter.getYear(), dateFilter.getMonth());
        return Map.of("data", incomeSummary);
    }

    /**
     * 获取支出数据
     *
     * @param dateFilter 日期过滤条件，包含年份和月份
     * @return 支出汇总数据
     */
    @PostMapping("/expenses")
    public Map<String, Object> getExpenses(@RequestBody DateFilter dateFilter) {
        // 获取按类别分组的支出数据
        Map<String, BigDecimal> expenseSummary = reportService.getExpenseData(dateFilter.getYear(), dateFilter.getMonth());
        return Map.of("data", expenseSummary);
    }

    @GetMapping("/incomes")
    public Map<String, BigDecimal> getIncomes(@RequestParam Integer year, @RequestParam(required = false) Integer month) {
        Map<String, BigDecimal> incomeSummary = reportService.getIncomeData(year, month);
        return incomeSummary; // 直接返回
    }


    @GetMapping("/expenses")
    public Map<String, BigDecimal> getExpense(@RequestParam Integer year, @RequestParam(required = false) Integer month) {
        Map<String, BigDecimal> expenseSummary = reportService.getExpenseData(year, month);
        return expenseSummary; // 直接返回
    }





}

