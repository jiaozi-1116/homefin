package com.example.yz.server.repository;

import com.example.yz.server.pojo.Income;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IncomeRepository extends JpaRepository<Income, Integer> {

    // 获取某一年的所有收入
    @Query("SELECT i FROM Income i WHERE YEAR(i.date) = :year")
    List<Income> findByDateYear(Integer year);

    // 获取某一年某一月的收入
    @Query("SELECT i FROM Income i WHERE YEAR(i.date) = :year AND MONTH(i.date) = :month")
    List<Income> findByDateYearAndDateMonth(Integer year, Integer month);

    //获取用户的收入
    List<Income> findByUserId(Integer userId);
    List<Income> findByFamilyId(Integer familyId);

    @Query("SELECT SUM(i.amount) FROM Income i " +
            "WHERE i.familyId = :familyId " +
            "AND YEAR(i.date) = :year " +
            "AND (:month IS NULL OR MONTH(i.date) = :month)")
    BigDecimal getTotalIncomeByFamilyAndPeriod(
            @Param("familyId") Integer familyId,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    @Query("SELECT i.source AS category, SUM(i.amount) AS total " +
            "FROM Income i " +
            "WHERE YEAR(i.date) = :year " +
            "AND (:month IS NULL OR MONTH(i.date) = :month) " +
            "GROUP BY i.source")
    Map<String, BigDecimal> getIncomeSummaryByPeriod(
            @Param("year") Integer year,
            @Param("month") Integer month
    );

}
