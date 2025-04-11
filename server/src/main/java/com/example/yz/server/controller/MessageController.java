package com.example.yz.server.controller;

import com.example.yz.server.dto.MessageRequest;
import com.example.yz.server.pojo.Message;
import com.example.yz.server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/families/{familyId}/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    public ResponseEntity<List<Message>> getMessages(@PathVariable Integer familyId) {
        List<Message> messages = messageService.getMessagesByFamilyId(familyId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    public ResponseEntity<Message> addMessage(
            @PathVariable Integer familyId,
            @RequestBody MessageRequest request) {
        Message message = messageService.addMessage(familyId, request.getUserId(), request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
}

