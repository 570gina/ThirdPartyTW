package com.gerrnbutton.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "authorization")
public class Authorization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authorization_id", length = 10)
    private int id;

    @Column(name = "number", length = 30)
    private String 	number;

    @Column(name = "access_token", length = 1000)
    private String 	accessToken;

    @Column(name = "refresh_token", length = 1000)
    private String refreshToken;

    @Column(name = "expires_in", length = 30)
    private String 	expiresIn;

    @Column(name = "token_type", length = 30)
    private String tokenType;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
