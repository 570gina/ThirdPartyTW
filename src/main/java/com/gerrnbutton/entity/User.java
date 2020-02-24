package com.gerrnbutton.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", length = 10)
    private int id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "name", length = 30)
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}
