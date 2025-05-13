package com.example.yz.server.controller;

import com.example.yz.server.dto.FinancialRecord;
import com.example.yz.server.pojo.Expense;
import com.example.yz.server.pojo.Income;
import com.example.yz.server.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
@CrossOrigin(origins = "*")
@Validated
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @GetMapping("/personal-financial-data")
    public ResponseEntity<Map<String, Object>> getPersonalFinancialData(
            @RequestParam Integer userId,
            @RequestParam Integer year) {

        Map<String, Object> data = calendarService.getPersonalFinancialData(userId, year);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/family-financial-data")
    public ResponseEntity<Map<String, Object>> getFamilyFinancialData(
            @RequestParam Integer userId,
            @RequestParam Integer year) {

        Map<String, Object> data = calendarService.getFamilyFinancialData(userId, year);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/personal-records")
    public ResponseEntity<List<FinancialRecord>> getPersonalRecords(
            @RequestParam Integer userId,
            @RequestParam String date) {

        List<FinancialRecord> records = calendarService.getPersonalRecords(userId, date);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/family-records")
    public ResponseEntity<List<FinancialRecord>> getFamilyRecords(
            @RequestParam Integer userId,
            @RequestParam String date) {

        List<FinancialRecord> records = calendarService.getFamilyRecords(userId, date);
        return ResponseEntity.ok(records);
    }
}
