package com.example.yz.server.pojo;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "notices")
@Data
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NoticeID")
    private int noticeId;

    @Column(name = "FamilyID")
    private Integer familyId;

    @Column(name = "AdminID")
    private int adminId;

    @Column(name = "Title", length = 100)
    private String title;

    @Column(name = "Content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "date_posted")
    private Date datePosted;
}
