package com.example.yz.server.pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "financial_materials")
public class FinancialMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer materialID;

    @ManyToOne
    @JoinColumn(name = "FamilyID", referencedColumnName = "FamilyID")
    private Family family;

    @ManyToOne
    @JoinColumn(name = "AdvisorID", referencedColumnName = "UserID", nullable = false)
    private User advisor;

    @Column(nullable = false)
    private String title;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    private MaterialCategory category;

    private String attachment;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime publishDate;

    public Integer getFamilyID() {
        return family.getFamilyID();
    }

    public Integer getAdvisorID() {
        return advisor.getUserId();
    }

    // 枚举定义
    public enum MaterialCategory {
        财务知识, 宣传材料, 投资指南
    }

    // Getters and Setters
}
