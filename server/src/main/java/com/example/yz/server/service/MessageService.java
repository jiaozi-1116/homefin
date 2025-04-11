package com.example.yz.server.service;

import com.example.yz.server.pojo.Message;
import com.example.yz.server.repository.FamilyMemberRepository;
import com.example.yz.server.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository; // 假设已存在

    public List<Message> getMessagesByFamilyId(Integer familyId) {
        return messageRepository.findByFamilyId(familyId);
    }

    public Message addMessage(Integer familyId, Integer userId, String content) {
//        if (!familyMemberRepository.existsByFamilyIdAndUserId(familyId, userId)) {
//            throw new IllegalArgumentException("User is not a member of the family.");
//        }
        Message message = new Message();
        message.setFamilyId(familyId);
        message.setUserId(userId);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        return messageRepository.save(message);
    }
}

