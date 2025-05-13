package com.example.yz.server.controller;

import com.example.yz.server.dto.CategoryRankDTO;
import com.example.yz.server.dto.CategoryStatsDTO;
import com.example.yz.server.dto.StatisticResult;
import com.example.yz.server.service.FinanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/finance")
@CrossOrigin(origins = "*")
@Validated
public class FinanceController {
    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/{type}/range")
    public ResponseEntity<List<StatisticResult>> getFinanceRange(
            @PathVariable String type,
            @RequestParam Integer memberId, // 对应UserID
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
            @RequestParam String groupBy // "day"或"month"
    ) {
        // 校验groupBy参数
        if (!"day".equals(groupBy) && !"month".equals(groupBy)) {
            throw new IllegalArgumentException("groupBy必须是'day'或'month'");
        }

        List<StatisticResult> result = financeService.statFinance(type, memberId, start, end, groupBy);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{type}/categories")
    public ResponseEntity<List<CategoryStatsDTO>> getCategoryStats(
            @PathVariable String type,
            @RequestParam Integer memberId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {

        List<CategoryStatsDTO> stats = financeService.getCategoryStats(type, memberId, start, end);
        return ResponseEntity.ok(stats);
    }

    // FinanceStatsController.java 新增端点
    @GetMapping("/{type}/ranking")
    public ResponseEntity<List<CategoryRankDTO>> getCategoryRanking(
            @PathVariable String type,
            @RequestParam Integer memberId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {

        List<CategoryRankDTO> ranking = financeService.getCategoryRank(type, memberId, start, end);
        return ResponseEntity.ok(ranking);
    }


}
