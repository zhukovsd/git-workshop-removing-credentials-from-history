package ru.prplhd.weather.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.prplhd.weather.dto.auth.SignInDto;
import ru.prplhd.weather.dto.auth.SignUpDto;
import ru.prplhd.weather.exception.auth.InvalidCredentialsException;
import ru.prplhd.weather.exception.auth.LoginAlreadyExistsException;
import ru.prplhd.weather.entity.UserEntity;
import ru.prplhd.weather.repository.UserRepository;
import ru.prplhd.weather.util.ConstraintViolationHandler;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConstraintViolationHandler constraintViolationHandler;
    private final SessionService sessionService;

    @Transactional
    public void signUp(SignUpDto signUpDto) {
        String hashedPassword = passwordEncoder.encode(signUpDto.password());
        String login = signUpDto.login();

        UserEntity newUser = new UserEntity(login, hashedPassword);

        try {
            userRepository.saveAndFlush(newUser);

        } catch (DataIntegrityViolationException e) {
            if (constraintViolationHandler.isLoginUniqueConstraintViolation(e)) {
                throw new LoginAlreadyExistsException("This login is already taken");
            }

            throw e;
        }
    }

    @Transactional
    public UUID signIn(SignInDto signInDto) {
        String login = signInDto.login();
        String rawPassword = signInDto.password();

        UserEntity userEntity = userRepository.findByLoginIgnoreCase(login)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid login or password"));

        String passwordHash = userEntity.getPasswordHash();
        boolean passwordsMatches = passwordEncoder.matches(rawPassword, passwordHash);

        if (!passwordsMatches) {
            throw new InvalidCredentialsException("Invalid login or password");
        }

        return sessionService.createSession(userEntity);
    }

    @Transactional
    public void signOut(UUID sessionId) {
        sessionService.deleteSession(sessionId);
    }
}
