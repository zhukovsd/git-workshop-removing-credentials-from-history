package ru.prplhd.weather.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.prplhd.weather.config.WeatherIntegrationTest;
import ru.prplhd.weather.dto.auth.SignInDto;
import ru.prplhd.weather.dto.auth.SignUpDto;
import ru.prplhd.weather.entity.SessionEntity;
import ru.prplhd.weather.entity.UserEntity;
import ru.prplhd.weather.exception.auth.InvalidCredentialsException;
import ru.prplhd.weather.exception.auth.LoginAlreadyExistsException;
import ru.prplhd.weather.repository.SessionRepository;
import ru.prplhd.weather.repository.UserRepository;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WeatherIntegrationTest
@Tag("auth")
class AuthServiceTest {

    private static final String VALID_LOGIN = "prplhd";
    private static final String VALID_PASSWORD = "123qwe";

    private static final String UNKNOWN_LOGIN = "doradura";
    private static final String WRONG_PASSWORD = "maybebaby322";

    private static final SignUpDto VALID_SIGN_UP_DTO = new SignUpDto(VALID_LOGIN, VALID_PASSWORD, VALID_PASSWORD);
    private static final SignInDto VALID_SIGN_IN_DTO = new SignInDto(VALID_LOGIN, VALID_PASSWORD);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Clock clock;

    @Autowired
    private Duration sessionTtl;

    @Test
    @DisplayName("Sign up with valid data creates user in database")
    void whenSignUp_withValidDto_thenCreatesUser() {
        authService.signUp(VALID_SIGN_UP_DTO);

        Optional<UserEntity> optUserEntity = userRepository.findByLoginIgnoreCase(VALID_SIGN_UP_DTO.login());

        assertThat(optUserEntity).isPresent();
    }

    @Test
    @DisplayName("Sign up with duplicate login throws UserAlreadyExistsException")
    void whenSignUp_withExistingLogin_thenThrowsException() {
        userRepository.save(new UserEntity(VALID_SIGN_UP_DTO.login(), passwordEncoder.encode(VALID_PASSWORD)));

        assertThatThrownBy(() -> authService.signUp(VALID_SIGN_UP_DTO))
                .isInstanceOf(LoginAlreadyExistsException.class);
    }

    @Test
    @DisplayName("Sing in with valid credentials creates session")
    void whenSignIn_withValidCredentials_thenCreatesSession() {
        givenUserExistsInDb();

        UUID sessionId = authService.signIn(VALID_SIGN_IN_DTO);
        Optional<SessionEntity> sessionOpt = sessionRepository.findById(sessionId);

        assertThat(sessionOpt).isPresent();

        String sessionUserLogin = sessionOpt.get().getUser().getLogin();

        assertThat(sessionUserLogin).isEqualTo(VALID_SIGN_IN_DTO.login());
    }

    @Test
    @DisplayName("Sign in with unknown login throws InvalidCredentialsException")
    void whenSignIn_withUnknownLogin_thenThrowsException() {
        SignInDto dtoWithUnknownLogin = new SignInDto(UNKNOWN_LOGIN, VALID_PASSWORD);

        assertThatThrownBy(() -> authService.signIn(dtoWithUnknownLogin))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("Sign in with wrong password throws InvalidCredentialsException")
    void whenSignIn_withWrongPassword_thenThrowsException() {
        givenUserExistsInDb();

        SignInDto dtoWithWrongPassword = new SignInDto(VALID_LOGIN, WRONG_PASSWORD);

        assertThatThrownBy(() -> authService.signIn(dtoWithWrongPassword)).isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("Sign out deletes existing session")
    void whenSignOut_withExistingSession_thenDeletesSession() {
        SessionEntity session = givenActiveSessionExistsInDb();
        UUID sessionId = session.getId();

        assertThat(sessionRepository.findById(sessionId)).isPresent();

        authService.signOut(sessionId);

        assertThat(sessionRepository.findById(sessionId)).isEmpty();
    }

    private UserEntity givenUserExistsInDb() {
        String hashPassword = passwordEncoder.encode(VALID_PASSWORD);
        UserEntity user = new UserEntity(VALID_LOGIN, hashPassword);

        return userRepository.save(user);
    }

    private SessionEntity givenActiveSessionExistsInDb() {
        UUID sessionId = UUID.randomUUID();
        UserEntity user = givenUserExistsInDb();
        Instant expiresAt = clock.instant().plus(sessionTtl);

        SessionEntity session = new SessionEntity(sessionId, user, expiresAt);

        return sessionRepository.save(session);
    }
}
