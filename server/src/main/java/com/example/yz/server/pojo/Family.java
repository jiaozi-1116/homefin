package com.example.yz.server.pojo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "families")
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int familyID;

    @Column(name = "AdminID")
    private int adminID;

    @Column(name = "family_name")
    private String familyName;

}
