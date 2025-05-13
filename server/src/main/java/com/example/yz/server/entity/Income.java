package com.example.yz.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("incomes")
public class Income {

    @TableId(type = IdType.AUTO)
    @TableField("IncomeID")
    private Integer incomeId; // 收入ID

     @TableField("UserID")
    private Integer userId; // 用户ID

     @TableField("FamilyID")
    private Integer familyId; // 家庭ID

     @TableField("Amount")
    private BigDecimal amount; // 收入金额

     @TableField("Source")
    private String source; // 收入来源

     @TableField("Date")
    private Date date; // 收入日期

     @TableField("Description")
    private String description; // 描述

}
