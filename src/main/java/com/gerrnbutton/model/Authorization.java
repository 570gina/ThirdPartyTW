package com.gerrnbutton.model;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "authorization")
public class Authorization {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "authorization_id")
    private int id;

    @Column(name = "access_token")
    private String 	access_token;
    @Column(name = "refresh_token")
    private String refresh_token;
    @Column(name = "expires_in")
    private String 	expires_in;
    @Column(name = "token_type")
    private String token_type;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
