package com.example.yz.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yz.server.entity.PurchaseRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PurchaseRequestMapper extends BaseMapper<PurchaseRequest> {
    @Select("SELECT r.*, u.full_name as applicant " +
            "FROM purchase_requests r " +
            "JOIN users u ON r.UserID = u.UserID " +
            "WHERE r.FamilyID = #{familyId}")
        // 使用 List<Map<String, Object>> 作为返回类型
    List<Map<String, Object>> selectPendingRequests(Integer familyId);




}
