package ru.prplhd.weather.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.prplhd.weather.dto.auth.AuthenticatedUserDto;
import ru.prplhd.weather.mapper.AuthenticatedUserMapper;
import ru.prplhd.weather.entity.SessionEntity;
import ru.prplhd.weather.entity.UserEntity;
import ru.prplhd.weather.repository.SessionRepository;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SessionService {

    private final Clock clock;
    private final Duration sessionTtl;
    private final SessionRepository sessionRepository;
    private final AuthenticatedUserMapper authenticatedUserMapper;

    @Transactional
    public UUID createSession(UserEntity userEntity) {
        UUID sessionId = UUID.randomUUID();
        Instant createdAt = clock.instant();
        Instant expiresAt = createdAt.plus(sessionTtl);

        SessionEntity sessionEntity = new SessionEntity(
                sessionId,
                userEntity,
                expiresAt
        );

        sessionRepository.save(sessionEntity);

        return sessionId;
    }

    @Transactional
    public Optional<AuthenticatedUserDto> resolveUserBySessionId(UUID sessionId) {
        Optional<SessionEntity> sessionEntityOpt = sessionRepository.findBySessionIdWithUser(sessionId);

        if (sessionEntityOpt.isEmpty()) {
            return Optional.empty();
        }

        SessionEntity sessionEntity = sessionEntityOpt.get();

        Instant now = clock.instant();
        boolean isSessionExpired = !sessionEntity.getExpiresAt().isAfter(now);

        if (isSessionExpired) {
            sessionRepository.delete(sessionEntity);
            return Optional.empty();
        }

        UserEntity userEntity = sessionEntity.getUser();

        AuthenticatedUserDto authenticatedUserDto = authenticatedUserMapper.toAuthenticatedUserDto(userEntity);

        return Optional.of(authenticatedUserDto);
    }

    @Transactional
    public void deleteSession(UUID sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    @Transactional
    public void deleteExpiredSessions() {
        sessionRepository.deleteExpiredSessions(clock.instant());
    }
}
