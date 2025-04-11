package com.example.yz.server.service;


import com.example.yz.server.pojo.Suggestion;
import com.example.yz.server.repository.SuggestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SuggestionService {

    @Autowired
    private SuggestionRepository SuggestionRepository;

    // 获取所有建议
    public List<Suggestion> getAllSuggestions() {
        return SuggestionRepository.findAll();
    }

    // 根据家庭ID获取建议
    public List<Suggestion> getSuggestionsByFamilyId(int familyId) {
        return SuggestionRepository.findByFamilyId(familyId);
    }



    // 删除公告
    public void deleteSuggestion(int SuggestionId) {
        SuggestionRepository.deleteById(SuggestionId);
    }

    //member
    // 根据 userId 获取所有建议
    public List<Suggestion> getSuggestionsByUserId(int userId) {
        return SuggestionRepository.findByUserId(userId);
    }

    // 提交新的建议
    public Suggestion submitSuggestion(Suggestion suggestion) {
        // 设置当前时间为建议提交时间
        suggestion.setDateSubmitted(new Date());
        return SuggestionRepository.save(suggestion);
    }

    public Suggestion updateSuggestionStatus(int suggestionId, String status) {
        // 查找建议
        Optional<Suggestion> suggestionOpt = SuggestionRepository.findById(suggestionId);

        if (suggestionOpt.isPresent()) {
            Suggestion suggestion = suggestionOpt.get();
            suggestion.setStatus(status);  // 更新状态
            return SuggestionRepository.save(suggestion);  // 保存更新后的建议
        } else {
            System.out.println("Suggestion not found with id " + suggestionId);
        }
        return null;
    }


}
