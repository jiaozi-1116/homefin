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
@TableName("expenses")
public class Expense {

    @TableId(type = IdType.AUTO)
     @TableField("ExpenseID")
    private Integer expenseId; // 支出ID

     @TableField("UserID")
    private Integer userId; // 用户ID

     @TableField("FamilyID")
    private Integer familyId; // 家庭ID

     @TableField("Amount")
    private BigDecimal amount; // 支出金额

     @TableField("Category")
    private String category; // 支出类别

     @TableField("Date")
    private Date date; // 支出日期

     @TableField("Description")
    private String description; // 描述

}
