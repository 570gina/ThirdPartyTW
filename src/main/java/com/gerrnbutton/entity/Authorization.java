package com.gerrnbutton.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "authorization")
public class Authorization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authorization_id")
    private int id;

    @Column(name = "number")
    private String 	number;

    @Column(name = "access_token")
    private String 	accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "expires_in")
    private String 	expiresIn;

    @Column(name = "token_type")
    private String tokenType;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
