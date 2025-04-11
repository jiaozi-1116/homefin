package com.example.yz.server.pojo;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "family_id")
    private Integer familyId;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "content")
    private String content;
    @Column(name = "attachment")
    private String attachment;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
