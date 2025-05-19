package com.example.yz.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("families")
public class Family {

    @TableId(type = IdType.AUTO)
    @TableField("familyID")
    private int familyID;

    @TableField("AdminID")
    private int adminID;

    @TableField("family_name")
    private String familyName;

}
