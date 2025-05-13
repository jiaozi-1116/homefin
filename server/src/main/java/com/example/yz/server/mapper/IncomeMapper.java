package com.example.yz.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yz.server.dto.*;
import com.example.yz.server.pojo.Expense;
import com.example.yz.server.pojo.Income;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface IncomeMapper extends BaseMapper<com.example.yz.server.entity.Income> {

    @Select("SELECT DATE_FORMAT(i.date, '%Y-%m-%d') AS date, SUM(i.amount) AS amount " +
            "FROM incomes i " +
            "WHERE i.userId = #{userId} " +
            "AND i.date BETWEEN #{start} AND #{end} " +
            "GROUP BY DATE_FORMAT(i.date, '%Y-%m-%d')")
    List<StatisticResult> statByDay(
            @Param("userId") Integer userId,
            @Param("start") Date start,
            @Param("end") Date end
    );

    @Select("SELECT DATE_FORMAT(i.date, '%Y-%m') AS date, SUM(i.amount) AS amount " +
            "FROM incomes i " +
            "WHERE i.userId = #{userId} " +
            "AND i.date BETWEEN #{start} AND #{end} " +
            "GROUP BY DATE_FORMAT(i.date, '%Y-%m')")
    List<StatisticResult> statByMonth(
            @Param("userId") Integer userId,
            @Param("start") Date start,
            @Param("end") Date end
    );

    @Select("SELECT Source as category, SUM(Amount) as amount " +
            "FROM incomes " +
            "WHERE UserID = #{userId} AND Date BETWEEN #{start} AND #{end} " +
            "GROUP BY Source")
    List<CategoryStatsDTO> getCategoryStats(
            @Param("userId") Integer userId,
            @Param("start") Date start,
            @Param("end") Date end);

    @Select("SELECT Source as category, COUNT(*) as count, SUM(Amount) as amount " +
            "FROM incomes " +
            "WHERE UserID = #{userId} AND Date BETWEEN #{start} AND #{end} " +
            "GROUP BY Source " +
            "ORDER BY amount DESC")
    List<CategoryRankDTO> getCategoryRank(
            @Param("userId") Integer userId,
            @Param("start") Date start,
            @Param("end") Date end);

    //日历
    @Select("SELECT * FROM homefin.incomes")
    List<Income> findAll();


    @Select("SELECT MONTH(date) as month, SUM(amount) as total " +
            "FROM incomes " +
            "WHERE FamilyID = #{familyId} AND YEAR(date) = #{year} " +
            "GROUP BY MONTH(date)")
    List<MonthlySummary> getMonthlyIncomeSummary(@Param("familyId") Integer familyId,
                                                 @Param("year") Integer year);


}

