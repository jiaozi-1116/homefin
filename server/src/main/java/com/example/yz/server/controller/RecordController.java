package com.example.yz.server.controller;

import com.example.yz.server.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RecordController {
    @Autowired
    private RecordService recordService;

    @GetMapping("/records")
    public List<Object> getRecords() {
        return recordService.getRecords();
    }
}
