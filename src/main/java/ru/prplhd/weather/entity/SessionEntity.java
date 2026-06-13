package ru.prplhd.weather.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sessions")
@Entity
public class SessionEntity {

    public SessionEntity(UUID id, UserEntity user, Instant expiresAt) {
        this.id = id;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    @Id
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserEntity user;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;
}
