package com.example.yz.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yz.server.dto.*;
import com.example.yz.server.entity.Expense;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Mapper
public interface ExpenseMapper extends BaseMapper<Expense>{
    @Select("SELECT Category, SUM(Amount) AS total_amount " +
            "FROM expenses " +
            "WHERE FamilyID = #{familyId} " +
            "AND Date BETWEEN #{start} AND #{end} " +
            "GROUP BY Category")
    @Results({
            @Result(property = "category", column = "Category"),
            @Result(property = "totalAmount", column = "total_amount")
    })
    List<CategorySummaryDTO> getCategorySummary(
            @Param("familyId") Integer familyId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);


    @Select("SELECT Category, SUM(Amount) AS total_amount " +
            "FROM expenses " +
            "WHERE UserID = #{memberId} " +
            "AND Date BETWEEN #{start} AND #{end} " +
            "GROUP BY Category")
    @Results({
            @Result(property = "category", column = "Category"),
            @Result(property = "totalAmount", column = "total_amount")
    })
    List<CategorySummaryDTO> getCategorySummaryUserId(
            @Param("memberId") Integer memberId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);


    @Select("SELECT DATE_FORMAT(e.date, '%Y-%m-%d') AS date, SUM(e.amount) AS amount " +
            "FROM expenses e " +
            "WHERE e.userId = #{userId} " +
            "AND e.date BETWEEN #{start} AND #{end} " +
            "GROUP BY DATE_FORMAT(e.date, '%Y-%m-%d')")
    List<StatisticResult> statByDay(
            @Param("userId") Integer userId,
            @Param("start") Date start,
            @Param("end") Date end
    );

    @Select("SELECT DATE_FORMAT(e.date, '%Y-%m') AS date, SUM(e.amount) AS amount " +
            "FROM expenses e " +
            "WHERE e.userId = #{userId} " +
            "AND e.date BETWEEN #{start} AND #{end} " +
            "GROUP BY DATE_FORMAT(e.date, '%Y-%m')")
    List<StatisticResult> statByMonth(
            @Param("userId") Integer userId,
            @Param("start") Date start,
            @Param("end") Date end
    );


    @Select("SELECT Category as category, SUM(Amount) as amount " +
            "FROM expenses " +
            "WHERE UserID = #{userId} AND Date BETWEEN #{start} AND #{end} " +
            "GROUP BY Category")
    List<CategoryStatsDTO> getCategoryStats(
            @Param("userId") Integer userId,
            @Param("start") Date start,
            @Param("end") Date end);

    @Select("SELECT Category as category, COUNT(*) as count, SUM(Amount) as amount " +
            "FROM expenses " +
            "WHERE UserID = #{userId} AND Date BETWEEN #{start} AND #{end} " +
            "GROUP BY Category " +
            "ORDER BY amount DESC")
    List<CategoryRankDTO> getCategoryRank(
            @Param("userId") Integer userId,
            @Param("start") Date start,
            @Param("end") Date end);

//日历
        @Select("SELECT * FROM expenses")
        List<Expense> findAll();


    @Select("SELECT " +// 这里简单用 UserID 代替姓名，实际应从用户表获取
            " *" +
            "FROM " +
            "    expenses e " +
            "WHERE " +
            "    e.FamilyID = #{familyId} " +
            "    AND e.Category = #{category} " +
            "    AND e.Date BETWEEN #{startDate} AND #{endDate} ")
    List<Expense> getMemberCategorySummary(
            @Param("familyId") Integer familyId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("category") String category
    );

    @Select("SELECT " +// 这里简单用 UserID 代替姓名，实际应从用户表获取
            " *" +
            "FROM " +
            "    expenses e " +
            "WHERE " +
            "    e.UserID = #{userId} " +
            "    AND e.Category = #{category} " +
            "    AND e.Date BETWEEN #{startDate} AND #{endDate} ")
    List<Expense> getMemberCategory(
            @Param("userId") Integer userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("category") String category
    );


    @Select("SELECT MONTH(date) as month, SUM(amount) as total " +
            "FROM expenses " +
            "WHERE FamilyID = #{familyId} AND YEAR(date) = #{year} " +
            "GROUP BY MONTH(date)")
    List<MonthlySummary> getMonthlyExpenseSummary(@Param("familyId") Integer familyId,
                                                  @Param("year") Integer year);







}
