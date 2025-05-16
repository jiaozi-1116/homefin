package com.example.yz.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("purchase_requests")
public class PurchaseRequest {
    @TableId(value = "RequestID", type = IdType.AUTO)
    private Integer requestId;

    @TableField("UserID")
    private Integer userId;

    @TableField("FamilyID")
    private Integer familyId;

    @TableField("ItemName")
    private String itemName;

    @TableField("Amount")
    private BigDecimal amount;

    @TableField("Description")
    private String description;

    @TableField("Status")
    private String status;

    @TableField("ApplyTime")
    private LocalDateTime applyTime;

    @TableField("AdminID")
    private Integer adminId;

    @TableField("ApproveTime")
    private LocalDateTime approveTime;

    @TableField("Deadline")
    private LocalDateTime deadline;
}
