package com.example.yz.server.pojo;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "suggestions")
@Data
public class Suggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SuggestionID")
    private int suggestionID;

    @Column(name = "UserID")
    private Integer userId;

    @Column(name = "FamilyID")
    private Integer familyId;

    @Column(name = "Content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "date_submitted")
    private Date dateSubmitted;

    @Column(name = "Status")
    private String status;

    @Column(name = "Title")
    private String title;

}
