package com.example.yz.server.repository;

import com.example.yz.server.pojo.Family;
import com.example.yz.server.pojo.FinancialMaterial;
import com.example.yz.server.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FinancialMaterialRepository extends JpaRepository<FinancialMaterial, Integer>, JpaSpecificationExecutor<FinancialMaterial> {

    // 根据顾问查询
    List<FinancialMaterial> findByAdvisor(User advisor);

    // 根据家庭查询
    List<FinancialMaterial> findByFamily(Family family);

    // 综合查询
    @Query("SELECT fm FROM FinancialMaterial fm WHERE " +
            "(:category IS NULL OR fm.category = :category) AND " +
            "(:advisor IS NULL OR fm.advisor = :advisor)")
    List<FinancialMaterial> searchMaterials(
            @Param("category") FinancialMaterial.MaterialCategory category,
            @Param("advisor") User advisor
    );
}
