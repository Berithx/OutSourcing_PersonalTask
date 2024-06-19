package com.sparta.spartime.entity;

import com.sparta.spartime.entity.common.TimeStamp;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
@Table(name = "users")
public class User extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String nickname;

    private String intro;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String refreshToken;

    private String recentPassword;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Role {
        USER,
        ADMIN
    }

    public enum Status {
        ACTIVITY,
        INACTIVITY,
        BLOCKED
    }

}