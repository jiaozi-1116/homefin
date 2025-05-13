package com.example.yz.server.repository;

import com.example.yz.server.dto.StatisticResult;
import com.example.yz.server.pojo.Expense;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    // 获取某一年的所有支出
    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :year")
    List<Expense> findByDateYear(Integer year);

    // 获取某一年某一月的支出
    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :year AND MONTH(e.date) = :month")
    List<Expense> findByDateYearAndDateMonth(Integer year, Integer month);

    List<Expense> findByUserId(Integer userId);

    @Query("SELECT e FROM Expense e WHERE e.familyId = :familyId " +
            "AND YEAR(e.date) = YEAR(CURRENT_DATE) " +
            "AND MONTH(e.date) = MONTH(CURRENT_DATE)")
    List<Expense> findByFamilyId(@Param("familyId") Integer familyId);

    @Query("SELECT SUM(e.amount) FROM Expense e " +
            "WHERE e.familyId = :familyId " +
            "AND YEAR(e.date) = :year " +
            "AND (:month IS NULL OR MONTH(e.date) = :month)")
    BigDecimal getTotalExpenseByFamilyAndPeriod(
            @Param("familyId") Integer familyId,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    @Query("SELECT e.category AS category, SUM(e.amount) AS total " +
            "FROM Expense e " +
            "WHERE YEAR(e.date) = :year " +
            "AND (:month IS NULL OR MONTH(e.date) = :month) " +
            "GROUP BY e.category")
    Map<String, BigDecimal> getExpenseSummaryByPeriod(
            @Param("year") Integer year,
            @Param("month") Integer month
    );



}
