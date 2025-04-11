package com.example.yz.server.dto;


import lombok.Data;

@Data
public class MessageRequest {
    private Integer userId;
    private String content;
}
