package com.example.yz.server.repository;

import com.example.yz.server.pojo.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    // 根据 UserID 查找预算
    List<Budget> findByUserId(Integer userId);

    // 根据 FamilyID 查找预算
    List<Budget> findByFamilyId(Integer familyId);

    // 根据 Category 查找预算
    List<Budget> findByCategory(String category);

    // 根据金额范围查找预算
    List<Budget> findByAmountBetween(Double minAmount, Double maxAmount);

    // 根据起始日期范围查找预算
    List<Budget> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    // 根据 FamilyID 和 Category 查找预算
    List<Budget> findByFamilyIdAndCategory(Integer familyId, String category);
}