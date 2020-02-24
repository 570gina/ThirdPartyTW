package com.gerrnbutton.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", length = 10)
    private int id;

    @Column(name = "role", length = 10)
    private String role;
}
