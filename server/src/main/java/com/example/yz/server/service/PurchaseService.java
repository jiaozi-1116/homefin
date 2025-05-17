package com.example.yz.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yz.server.entity.PurchaseImage;
import com.example.yz.server.entity.PurchaseRequest;
import com.example.yz.server.mapper.PurchaseImageMapper;
import com.example.yz.server.mapper.PurchaseRequestMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PurchaseService {


    private  final PurchaseRequestMapper requestMapper;

    private  final PurchaseImageMapper imageMapper;


    //用户创建申请
    @Transactional
    public void createRequest(PurchaseRequest request) {
//        request.setUserId(userService.getCurrentUserId());
//        request.setFamilyId(userService.getCurrentUserFamilyId());
        request.setStatus("Pending");
        request.setApplyTime(LocalDateTime.now());
        requestMapper.insert(request);
    }

    @Transactional
    public void approveRequest(Integer requestId) {
        PurchaseRequest request = requestMapper.selectById(requestId);
        request.setStatus("Approved");
//        request.setAdminId(userService.getCurrentUserId());
        request.setApproveTime(LocalDateTime.now());
        request.setDeadline(LocalDateTime.now().plusDays(3));
        requestMapper.updateById(request);
    }

    @Transactional
    public void rejectRequest(Integer requestId) {
        PurchaseRequest request = requestMapper.selectById(requestId);
        request.setStatus("Rejected");
//        request.setAdminId(userService.getCurrentUserId());
        request.setApproveTime(LocalDateTime.now());
        request.setDeadline(LocalDateTime.now().plusDays(3));
        requestMapper.updateById(request);
    }

    public List<PurchaseRequest> getUserRequests(Integer userId) {
        return requestMapper.selectList(
                new QueryWrapper<PurchaseRequest>()
                        .eq("UserID", userId)
                        .orderByDesc("ApplyTime")
        );
    }

    public List<Map<String, Object>> getMembersRequests(Integer familyId) {
        return requestMapper.selectPendingRequests(familyId);
    }

    public PurchaseRequest detail (Integer request){
        return requestMapper.selectById(request);
    }

    public List<PurchaseImage> getImage (Integer request){
        return imageMapper.selectByRequestId(request);
    }
}
