package com.example.yz.server.controller;

import com.example.yz.server.dto.MonthlyTrendResponse;
import com.example.yz.server.service.FinancialTrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/financial")
@CrossOrigin(origins = "*")
public class FinancialTrendController {

    @Autowired
    private  FinancialTrendService financialTrendService;

    @GetMapping("/trend")
    public List<MonthlyTrendResponse> getFinancialTrend(
            @RequestParam Integer familyId,
            @RequestParam Integer year) {
        return financialTrendService.getFinancialTrend(familyId, year);

    }
}
