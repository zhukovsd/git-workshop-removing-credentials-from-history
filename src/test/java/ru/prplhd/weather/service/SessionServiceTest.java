package ru.prplhd.weather.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.prplhd.weather.config.AppConfig;
import ru.prplhd.weather.config.DataSourceConfig;
import ru.prplhd.weather.config.JpaConfig;
import ru.prplhd.weather.config.LiquibaseConfig;
import ru.prplhd.weather.dto.auth.AuthenticatedUserDto;
import ru.prplhd.weather.persistence.entity.SessionEntity;
import ru.prplhd.weather.persistence.entity.UserEntity;
import ru.prplhd.weather.persistence.repository.SessionRepository;
import ru.prplhd.weather.persistence.repository.UserRepository;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {
        AppConfig.class,
        DataSourceConfig.class,
        JpaConfig.class,
        LiquibaseConfig.class
})
@TestPropertySource("classpath:app-test.properties")
@Transactional
@Tag("auth")
public class SessionServiceTest {

    private static final String LOGIN = "prplhd";
    private static final String PASSWORD = "password1337";

    @Autowired
    SessionService sessionService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    Clock clock;

    @Autowired
    Duration sessionTtl;

    @Test
    @DisplayName("Create session for existing user creates session in database")
    void whenCreateSession_withExistingUser_thenCreatesSession() {
        UserEntity user = givenUserExistsInDb();

        UUID sessionId = sessionService.createSession(user);
        Optional<SessionEntity> sessionOpt = sessionRepository.findById(sessionId);

        assertThat(sessionOpt).isPresent();

        Instant expiresAt = sessionOpt.get().getExpiresAt();

        assertThat(expiresAt).isAfter(clock.instant());

        Long sessionUserId = sessionOpt.get().getUser().getId();
        Long userId = user.getId();

        assertThat(sessionUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("Resolve user by active session returns authenticated user")
    void whenResolveUserBySessionId_withActiveSession_thenReturnsAuthenticatedUser() {
        SessionEntity session = givenActiveSessionExistsInDb();
        UUID sessionId = session.getId();

        Optional<AuthenticatedUserDto> authenticatedUserOpt = sessionService.resolveUserBySessionId(sessionId);

        assertThat(authenticatedUserOpt).isPresent();

        String sessionUserLogin = session.getUser().getLogin();
        String userLogin = authenticatedUserOpt.get().login();

        assertThat(sessionUserLogin).isEqualTo(userLogin);
    }

    @Test
    @DisplayName("Resolve user by unknown session returns empty result")
    void whenResolveUserBySessionId_withUnknownSession_thenReturnsEmpty() {
        UUID unknownSessionId = UUID.randomUUID();

        Optional<AuthenticatedUserDto> authenticatedUserOpt = sessionService.resolveUserBySessionId(unknownSessionId);

        assertThat(authenticatedUserOpt).isEmpty();
    }

    @Test
    @DisplayName("Resolve user by expired session returns empty result and deletes session")
    void whenResolveUserBySessionId_withExpiredSession_thenReturnsEmptyAndDeletesSession() {
        SessionEntity expiredSession = givenExpiredSessionExistsInDb();
        UUID expiredSessionId = expiredSession.getId();

        assertThat(sessionRepository.findById(expiredSessionId)).isPresent();

        Optional<AuthenticatedUserDto> authenticatedUserOpt = sessionService.resolveUserBySessionId(expiredSessionId);

        assertThat(authenticatedUserOpt).isEmpty();

        assertThat(sessionRepository.findById(expiredSessionId)).isEmpty();
    }

    private UserEntity givenUserExistsInDb() {
        String hashPassword = passwordEncoder.encode(PASSWORD);
        UserEntity user = new UserEntity(LOGIN, hashPassword);

        return userRepository.save(user);
    }

    private SessionEntity givenActiveSessionExistsInDb() {
        UUID sessionId = UUID.randomUUID();
        UserEntity user = givenUserExistsInDb();
        Instant expiresAt = clock.instant().plus(sessionTtl);

        SessionEntity session = new SessionEntity(sessionId, user, expiresAt);

        return sessionRepository.save(session);
    }

    private SessionEntity givenExpiredSessionExistsInDb() {
        UUID sessionId = UUID.randomUUID();
        UserEntity user = givenUserExistsInDb();
        Instant expiresAt = clock.instant().minusSeconds(1);

        SessionEntity session = new SessionEntity(sessionId, user, expiresAt);

        return sessionRepository.save(session);
    }
}
