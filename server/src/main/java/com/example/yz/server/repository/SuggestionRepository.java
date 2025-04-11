package com.example.yz.server.repository;


import com.example.yz.server.pojo.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Integer> {
    // 可以根据需要添加更多查询方法，例如按家庭ID查询公告
    List<Suggestion> findByFamilyId(int familyId);

    List<Suggestion> findByUserId(int userId);
    Suggestion findBySuggestionID(int suggestionId);



}
