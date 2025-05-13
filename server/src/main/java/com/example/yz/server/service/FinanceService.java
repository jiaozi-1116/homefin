package com.example.yz.server.service;

import com.example.yz.server.dto.CategoryRankDTO;
import com.example.yz.server.dto.CategoryStatsDTO;
import com.example.yz.server.dto.StatisticResult;
import com.example.yz.server.mapper.ExpenseMapper;
import com.example.yz.server.mapper.IncomeMapper;
import com.example.yz.server.repository.ExpenseRepository;
import com.example.yz.server.repository.IncomeRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class FinanceService {

    private final IncomeMapper incomeMapper;
    private final ExpenseMapper expenseMapper;

    public FinanceService(IncomeMapper incomeMapper, ExpenseMapper expenseMapper) {
        this.incomeMapper = incomeMapper;
        this.expenseMapper = expenseMapper;
    }

    /**
     * 通用统计方法
     * @param type 统计类型（"income"或"expense"）
     * @param userId 用户ID
     * @param start 开始日期
     * @param end 结束日期
     * @param groupBy 分组方式（"day"或"month"）
     * @return 统计结果列表
     */
    public List<StatisticResult> statFinance(
            String type, Integer userId, Date start, Date end, String groupBy
    ) {
        if (!"day".equals(groupBy) && !"month".equals(groupBy)) {
            throw new IllegalArgumentException("无效的分组方式：" + groupBy);
        }

        if ("income".equals(type)) {
            return "day".equals(groupBy)
                    ? incomeMapper.statByDay(userId, start, end)
                    : incomeMapper.statByMonth(userId, start, end);
        } else if ("expense".equals(type)) {
            return "day".equals(groupBy)
                    ? expenseMapper.statByDay(userId, start, end)
                    : expenseMapper.statByMonth(userId, start, end);
        } else {
            throw new IllegalArgumentException("无效的统计类型：" + type);
        }
    }

    public List<CategoryStatsDTO> getCategoryStats(String type, Integer userId, Date start, Date end) {
        if ("expense".equalsIgnoreCase(type)) {
            return expenseMapper.getCategoryStats(userId, start, end);
        } else if ("income".equalsIgnoreCase(type)) {
            return incomeMapper.getCategoryStats(userId, start, end);
        }
        throw new IllegalArgumentException("Invalid type parameter");
    }

    public List<CategoryRankDTO> getCategoryRank(String type, Integer userId, Date start, Date end) {
        if ("expense".equalsIgnoreCase(type)) {
            return expenseMapper.getCategoryRank(userId, start, end);
        } else if ("income".equalsIgnoreCase(type)) {
            return incomeMapper.getCategoryRank(userId, start, end);
        }
        throw new IllegalArgumentException("Invalid type parameter");
    }
}

