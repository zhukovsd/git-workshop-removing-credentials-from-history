package ru.prplhd.weather.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@ToString(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class UserEntity {

    public UserEntity(String login, String passwordHash) {
        this.login = login;
        this.passwordHash = passwordHash;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login", nullable = false, updatable = false)
    private String login;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
}