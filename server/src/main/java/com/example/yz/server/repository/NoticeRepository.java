package com.example.yz.server.repository;

import com.example.yz.server.pojo.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    // 可以根据需要添加更多查询方法，例如按家庭ID查询公告
    List<Notice> findByFamilyId(int familyId);

    List<Notice> findByAdminId(int adminId);
}
