package com.example.yz.server.service;

import com.example.yz.server.pojo.Income;
import com.example.yz.server.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    // 获取用户的收入记录
    public List<Income> getIncomesByUserId(Integer userId) {
        return incomeRepository.findByUserId(userId);
    }

    public List<Income> getIncomesByFamilyId(Integer familyId) {
        return incomeRepository.findByFamilyId(familyId);
    }

    // 添加收入记录
    public Income saveIncome(Income income) {
        return incomeRepository.save(income);
    }

    // 删除收入记录
    public void deleteIncome(Integer incomeId) {
        incomeRepository.deleteById(incomeId);
    }

    public List<Map<String, Object>> getIncomeData(Integer userId) {
        // 从数据库中查询指定用户的所有收入数据
        List<Income> incomeList = incomeRepository.findByUserId(userId);

        // 使用 Map 来按 'source' 分组并累加 'amount'
        Map<String, BigDecimal> incomeMap = incomeList.stream()
                .collect(Collectors.groupingBy(
                        Income::getSource,
                        Collectors.reducing(BigDecimal.ZERO, Income::getAmount, BigDecimal::add)
                ));

        // 将结果转换为前端需要的格式：x轴的值和y轴的值
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : incomeMap.entrySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("source", entry.getKey());
            data.put("amount", entry.getValue());
            result.add(data);
        }

        return result;
    }
}