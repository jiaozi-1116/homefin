package com.example.yz.server.repository;

import com.example.yz.server.pojo.FinancialReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialReportRepository extends JpaRepository<FinancialReport, Integer> {

    // 按家庭ID和报告类型查找报告
    List<FinancialReport> findByFamilyIdAndReportType(Integer familyId, String reportType);



    // 按报告生成日期范围查找报告
    List<FinancialReport> findByGeneratedDateBetween(LocalDate startDate, LocalDate endDate);

    List<FinancialReport> findByFamilyId (Integer familyId);
}

