package com.example.yz.server.controller;

import com.example.yz.server.pojo.Notice;
import com.example.yz.server.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin(origins = "*")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // 获取所有公告
    @GetMapping
    public ResponseEntity<List<Notice>> getAllNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        return new ResponseEntity<>(notices, HttpStatus.OK);
    }

    // 根据家庭ID获取公告
    @GetMapping("/family/{familyId}")
    public ResponseEntity<List<Notice>> getNoticesByFamilyId(@PathVariable int familyId) {
        List<Notice> notices = noticeService.getNoticesByFamilyId(familyId);
        return new ResponseEntity<>(notices, HttpStatus.OK);
    }

    // 发布新公告
    @PostMapping
    public ResponseEntity<Notice> publishNotice(@RequestBody Notice notice) {
        Notice createdNotice = noticeService.publishNotice(notice);
        return new ResponseEntity<>(createdNotice, HttpStatus.CREATED);
    }

    // 删除公告
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable int noticeId) {
        noticeService.deleteNotice(noticeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //member
    // 获取指定 adminId 的所有建议
    @GetMapping("/member/{adminId}")
    public List<Notice> getNotices(@PathVariable int adminId) {
        return noticeService.getNoticesByAdminId(adminId);
    }

    // 提交新的建议
    @PostMapping("/member")
    public Notice submitSuggestion(@RequestBody Notice notice) {
        if (notice.getFamilyId() == null) { System.out.println("FamilyID is null"); }
        return noticeService.submitSuggestion(notice);
    }


}
