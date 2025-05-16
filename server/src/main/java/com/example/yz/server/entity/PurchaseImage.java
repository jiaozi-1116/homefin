package com.example.yz.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("purchase_images")
public class PurchaseImage {
    @TableId(type = IdType.AUTO)
    private Integer imageId;
    private Integer requestId;
    private String imagePath;
    private LocalDateTime uploadTime;
}

