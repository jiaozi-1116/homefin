package com.example.yz.server.mapper;

import com.example.yz.server.dto.FinancialRecord;
import com.example.yz.server.dto.FinancialSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FinancialMapper {

    // 获取用户自己的每日收支汇总
    @Select("SELECT DATE_FORMAT(Date, '%Y-%m-%d') as date, " +
            "SUM(CASE WHEN Amount < 0 THEN ABS(Amount) ELSE 0 END) as expense, " +
            "SUM(CASE WHEN Amount > 0 THEN Amount ELSE 0 END) as income " +
            "FROM ( " +
            "   SELECT Amount, Date FROM incomes WHERE UserID = #{userId} AND YEAR(Date) = #{year} " +
            "   UNION ALL " +
            "   SELECT -Amount as Amount, Date FROM expenses WHERE UserID = #{userId} AND YEAR(Date) = #{year} " +
            ") t " +
            "GROUP BY date")
    List<FinancialSummary> getPersonalDailySummary(@Param("userId") Integer userId, @Param("year") Integer year);

    // 获取用户家庭的每日收支汇总
    @Select("SELECT DATE_FORMAT(Date, '%Y-%m-%d') as date, " +
            "SUM(CASE WHEN Amount < 0 THEN ABS(Amount) ELSE 0 END) as expense, " +
            "SUM(CASE WHEN Amount > 0 THEN Amount ELSE 0 END) as income " +
            "FROM ( " +
            "   SELECT Amount, Date FROM incomes WHERE (UserID = #{userId} OR FamilyID IN (SELECT FamilyID FROM familymembers WHERE UserID = #{userId})) AND YEAR(Date) = #{year} " +
            "   UNION ALL " +
            "   SELECT -Amount as Amount, Date FROM expenses WHERE (UserID = #{userId} OR FamilyID IN (SELECT FamilyID FROM familymembers WHERE UserID = #{userId})) AND YEAR(Date) = #{year} " +
            ") t " +
            "GROUP BY date")
    List<FinancialSummary> getFamilyDailySummary(@Param("userId") Integer userId, @Param("year") Integer year);

    // 获取用户自己的每月收支汇总
    @Select("SELECT DATE_FORMAT(Date, '%Y-%m') as date, " +
            "SUM(CASE WHEN Amount < 0 THEN ABS(Amount) ELSE 0 END) as expense, " +
            "SUM(CASE WHEN Amount > 0 THEN Amount ELSE 0 END) as income " +
            "FROM ( " +
            "   SELECT Amount, Date FROM incomes WHERE UserID = #{userId} AND YEAR(Date) = #{year} " +
            "   UNION ALL " +
            "   SELECT -Amount as Amount, Date FROM expenses WHERE UserID = #{userId} AND YEAR(Date) = #{year} " +
            ") t " +
            "GROUP BY DATE_FORMAT(Date, '%Y-%m')")
    List<FinancialSummary> getPersonalMonthlySummary(@Param("userId") Integer userId, @Param("year") Integer year);

    // 获取用户家庭的每月收支汇总
    @Select("SELECT DATE_FORMAT(Date, '%Y-%m') as date, " +
            "SUM(CASE WHEN Amount < 0 THEN ABS(Amount) ELSE 0 END) as expense, " +
            "SUM(CASE WHEN Amount > 0 THEN Amount ELSE 0 END) as income " +
            "FROM ( " +
            "   SELECT Amount, Date FROM incomes WHERE (UserID = #{userId} OR FamilyID IN (SELECT FamilyID FROM familymembers WHERE UserID = #{userId})) AND YEAR(Date) = #{year} " +
            "   UNION ALL " +
            "   SELECT -Amount as Amount, Date FROM expenses WHERE (UserID = #{userId} OR FamilyID IN (SELECT FamilyID FROM familymembers WHERE UserID = #{userId})) AND YEAR(Date) = #{year} " +
            ") t " +
            "GROUP BY DATE_FORMAT(Date, '%Y-%m')")
    List<FinancialSummary> getFamilyMonthlySummary(@Param("userId") Integer userId, @Param("year") Integer year);

    // 获取用户自己的详细记录
    @Select("SELECT DATE_FORMAT(Date, '%Y-%m-%d') as date, '收入' as type, Amount, Source as category, Description " +
            "FROM incomes " +
            "WHERE UserID = #{userId} " +
            "AND Date LIKE CONCAT(#{date}, '%') " +
            "UNION ALL " +
            "SELECT DATE_FORMAT(Date, '%Y-%m-%d') as date, '支出' as type, Amount, Category as category, Description " +
            "FROM expenses " +
            "WHERE UserID = #{userId} " +
            "AND Date LIKE CONCAT(#{date}, '%') " +
            "ORDER BY date DESC")
    List<FinancialRecord> getPersonalRecords(@Param("userId") Integer userId, @Param("date") String date);

    // 获取用户家庭的详细记录
    @Select("SELECT DATE_FORMAT(Date, '%Y-%m-%d') as date, '收入' as type, Amount, Source as category, Description " +
            "FROM incomes " +
            "WHERE (UserID = #{userId} OR FamilyID IN (SELECT FamilyID FROM familymembers WHERE UserID = #{userId})) " +
            "AND Date LIKE CONCAT(#{date}, '%') " +
            "UNION ALL " +
            "SELECT DATE_FORMAT(Date, '%Y-%m-%d') as date, '支出' as type, Amount, Category as category, Description " +
            "FROM expenses " +
            "WHERE (UserID = #{userId} OR FamilyID IN (SELECT FamilyID FROM familymembers WHERE UserID = #{userId})) " +
            "AND Date LIKE CONCAT(#{date}, '%') " +
            "ORDER BY date DESC")
    List<FinancialRecord> getFamilyRecords(@Param("userId") Integer userId, @Param("date") String date);
}