package com.example.yz.server.service;

import com.example.yz.server.mapper.ExpenseMapper;
import com.example.yz.server.mapper.IncomeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class RecordService {

    private final ExpenseMapper expensesMapper;
    private final IncomeMapper incomesMapper;

    public RecordService(ExpenseMapper expensesMapper, IncomeMapper incomesMapper) {
        this.expensesMapper = expensesMapper;
        this.incomesMapper = incomesMapper;
    }

    public List<Object> getRecords() {
        List<Object> records = new ArrayList<>();
        records.addAll(expensesMapper.findAll());
        records.addAll(incomesMapper.findAll());
        return records;
    }
}
