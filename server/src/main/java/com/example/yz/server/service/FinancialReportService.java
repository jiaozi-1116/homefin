package com.example.yz.server.service;

import com.example.yz.server.dto.ReportRequest;
import com.example.yz.server.pojo.Expense;
import com.example.yz.server.pojo.Family;
import com.example.yz.server.pojo.FinancialReport;
import com.example.yz.server.pojo.Income;
import com.example.yz.server.repository.ExpenseRepository;
import com.example.yz.server.repository.FamilyRepository;
import com.example.yz.server.repository.FinancialReportRepository;
import com.example.yz.server.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FinancialReportService {

    @Autowired
    private FinancialReportRepository reportRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private FamilyRepository familyRepository;

    // 获取家庭的财务报告
    public List<FinancialReport> getReportsByFamilyId(Integer familyId, String reportType) {
        return reportRepository.findByFamilyIdAndReportType(familyId, reportType);
    }

    // 生成财务报告
//    public FinancialReport generateReport(Integer familyId, Integer userId, String reportType, String data) {
//        FinancialReport report = new FinancialReport();
//        report.setFamilyId(familyId);
////        report.setUserId(userId);
//        report.setReportType(reportType);
//        report.setGeneratedDate(LocalDate.now());
////        report.setData(data);  // 将数据存储为JSON字符串
//        return reportRepository.save(report);
//    }



    /**
     * 获取收入数据
     *
     * @param year  年份
     * @param month 月份，若为 null 则不限定月份
     * @return 按来源分组的收入数据
     */
    public Map<String, BigDecimal> getIncomeData(Integer year, Integer month) {
        List<Income> incomes;

        if (month == null) {
            // 如果没有指定月份，按年份查询所有收入
            incomes = incomeRepository.findByDateYear(year);
        } else {
            // 如果指定了月份，按年份和月份查询收入
            incomes = incomeRepository.findByDateYearAndDateMonth(year, month);
        }

        // 按收入来源（source）分组并汇总金额
        return incomes.stream()
                .collect(Collectors.groupingBy(
                        Income::getSource, // 按来源分组
                        Collectors.reducing(BigDecimal.ZERO, Income::getAmount, BigDecimal::add) // 汇总金额
                ));
    }

    /**
     * 获取支出数据
     *
     * @param year  年份
     * @param month 月份，若为 null 则不限定月份
     * @return 按类别分组的支出数据
     */
    public Map<String, BigDecimal> getExpenseData(Integer year, Integer month) {
        List<Expense> expenses;

        if (month == null) {
            // 如果没有指定月份，按年份查询所有支出
            expenses = expenseRepository.findByDateYear(year);
        } else {
            // 如果指定了月份，按年份和月份查询支出
            expenses = expenseRepository.findByDateYearAndDateMonth(year, month);
        }

        // 按支出类别（category）分组并汇总金额
        return expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory, // 按类别分组
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add) // 汇总金额
                ));
    }


    public FinancialReport generateFamilyReport(Integer familyId, String reportType, Integer year, Integer month) {
        // 1. 验证参数
        if (familyId == null || reportType == null || year == null) {
            throw new IllegalArgumentException("家庭ID、报告类型和年份不能为空");
        }

        // 2. 查询收入数据
        BigDecimal totalIncome = incomeRepository.getTotalIncomeByFamilyAndPeriod(familyId, year, month);

        // 3. 查询支出数据
        BigDecimal totalExpense = expenseRepository.getTotalExpenseByFamilyAndPeriod(familyId, year, month);

        // 4. 计算财务健康指数
        BigDecimal financialHealthIndex = calculateFinancialHealthIndex(totalIncome, totalExpense);

        // 5. 创建财务报告
        FinancialReport report = new FinancialReport();
        report.setFamilyId(familyId);
        report.setReportType(reportType);
        report.setYear(year);
        report.setMonth(month);
        report.setGeneratedDate(LocalDate.from(LocalDateTime.now()));
        report.setFinancialHealthIndex(financialHealthIndex);

        // 6. 保存报告
        return reportRepository.save(report);
    }

    /**
     * 获取家庭的所有财务报告
     *
     * @param familyId 家庭ID
     * @return 财务报告列表
     */
    public List<FinancialReport> getReportsByFamily(Integer familyId) {
        if (familyId == null) {
            throw new IllegalArgumentException("家庭ID不能为空");
        }
        return reportRepository.findByFamilyId(familyId);
    }

    /**
     * 计算财务健康指数
     *
     * @param totalIncome  总收入
     * @param totalExpense 总支出
     * @return 财务健康指数（0-100）
     */
    private BigDecimal calculateFinancialHealthIndex(BigDecimal totalIncome, BigDecimal totalExpense) {
        if (totalIncome == null || totalExpense == null) {
            throw new IllegalArgumentException("收入和支出数据不能为空");
        }

        // 计算结余比例
        if (totalIncome.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO; // 无收入时健康指数为0
        }

        BigDecimal savings = totalIncome.subtract(totalExpense);
        BigDecimal savingsRatio = savings.divide(totalIncome, 2, BigDecimal.ROUND_HALF_UP);

        // 将结余比例转换为健康指数（0-100）
        BigDecimal healthIndex = savingsRatio.multiply(BigDecimal.valueOf(100));
        return healthIndex.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100)); // 确保在0-100之间
    }






}

