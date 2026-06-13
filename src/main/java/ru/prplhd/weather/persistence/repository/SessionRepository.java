package ru.prplhd.weather.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.prplhd.weather.persistence.entity.SessionEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {

    @Query("""
           SELECT s
           FROM SessionEntity s
           JOIN FETCH s.user
           WHERE s.id = :sessionId
           """)
    Optional<SessionEntity> findBySessionIdWithUser(@Param("sessionId") UUID sessionId);

    @Modifying
    @Query("""
           DELETE
           FROM SessionEntity s
           WHERE s.expiresAt <= :now
           """)
    void deleteExpiredSessions(@Param("now") Instant now);
}
