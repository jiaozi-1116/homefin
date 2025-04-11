package com.example.yz.server.pojo;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer userId;
    private String username;
    private String password;
    private String role;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Date dateRegistered;
}
