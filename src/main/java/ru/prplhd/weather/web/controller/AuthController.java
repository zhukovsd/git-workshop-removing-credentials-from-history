package ru.prplhd.weather.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.prplhd.weather.dto.auth.SignInDto;
import ru.prplhd.weather.dto.auth.SignUpDto;
import ru.prplhd.weather.exception.auth.InvalidCredentialsException;
import ru.prplhd.weather.exception.auth.LoginAlreadyExistsException;
import ru.prplhd.weather.service.AuthService;
import ru.prplhd.weather.web.auth.SessionCookieManager;
import ru.prplhd.weather.web.validation.SignUpDtoValidator;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final SignUpDtoValidator signUpDtoValidator;
    private final SessionCookieManager sessionCookieManager;

    public AuthController(AuthService authService, SignUpDtoValidator signUpDtoValidator, Duration sessionTtl, SessionCookieManager sessionCookieManager) {
        this.authService = authService;
        this.signUpDtoValidator = signUpDtoValidator;
        this.sessionCookieManager = sessionCookieManager;
    }

    @GetMapping("/sign-in")
    public String signInPage(@ModelAttribute("signInDto") SignUpDto signUpDto) {
        return "sign-in";
    }

    @GetMapping("/sign-up")
    public String signUpPage(@ModelAttribute("signUpDto") SignUpDto signUpDto) {
        return "sign-up";
    }

    @PostMapping("/sign-in")
    public String signIn(@ModelAttribute("signInDto") @Valid SignInDto signInDto,
                         BindingResult bindingResult,
                         HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            return "sign-in";
        }

        UUID sessionId;
        try {
            sessionId = authService.signIn(signInDto);
        } catch (InvalidCredentialsException e) {
            bindingResult.reject("invalid.credentials", e.getMessage());
            return "sign-in";
        }

        sessionCookieManager.addSessionCookie(sessionId, response);

        return "redirect:/";
    }

    @PostMapping("/sign-up")
    public String signUp(@ModelAttribute("signUpDto") @Valid SignUpDto signUpDto,
                          BindingResult bindingResult) {

        signUpDtoValidator.validate(signUpDto, bindingResult);

        if (bindingResult.hasErrors()) {
            return "sign-up";
        }

        try {
            authService.signUp(signUpDto);
        } catch (LoginAlreadyExistsException e) {
            bindingResult.rejectValue("login", "login.alreadyExists", e.getMessage());
            return "sign-up";
        }

        return "redirect:/auth/sign-in";
    }

    @PostMapping("/sign-out")
    public String signOut(HttpServletRequest request, HttpServletResponse response) {
        Optional<UUID> sessionIdOpt = sessionCookieManager.findSessionId(request);

        if (sessionIdOpt.isPresent()) {
            authService.signOut(sessionIdOpt.get());
        }

        sessionCookieManager.deleteSessionCookie(response);

        return "redirect:/auth/sign-in";
    }
}
