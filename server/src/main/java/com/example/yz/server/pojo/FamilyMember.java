package com.example.yz.server.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "familymembers")
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int MemberID;

    @Column(name = "UserID")
    private int userID;

    @Column(name = "FamilyID")
    private int familyID;
    private String Relationship;
}

