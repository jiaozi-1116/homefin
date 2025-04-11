package com.example.yz.server.service;

import com.example.yz.server.pojo.Notice;
import com.example.yz.server.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    // 获取所有公告
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    // 根据家庭ID获取公告
    public List<Notice> getNoticesByFamilyId(int familyId) {
        return noticeRepository.findByFamilyId(familyId);
    }

    // 发布新公告
    public Notice publishNotice(Notice notice) {
        notice.setDatePosted(new java.util.Date());  // 设置公告发布日期为当前日期
        return noticeRepository.save(notice);
    }

    // 删除公告
    public void deleteNotice(int noticeId) {
        noticeRepository.deleteById(noticeId);
    }

    //member
    // 根据 adminId 获取所有建议
    public List<Notice> getNoticesByAdminId(int adminId) {
        return noticeRepository.findByAdminId(adminId);
    }

    // 提交新的建议
    public Notice submitSuggestion(Notice notice) {
        // 设置当前时间为建议提交时间
        notice.setDatePosted(new Date());
        return noticeRepository.save(notice);
    }
}
