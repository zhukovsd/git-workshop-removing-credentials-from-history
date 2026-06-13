package ru.prplhd.weather.session;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionCookieManager {

    private static final String SESSION_COOKIE_NAME = "SESSION";

    private final Duration sessionTtl;

    public void addSessionCookie(UUID sessionId, HttpServletResponse response) {
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, sessionId.toString());

        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false);
        int cookieAge = Math.toIntExact(sessionTtl.toSeconds());
        cookie.setMaxAge(cookieAge);

        response.addCookie(cookie);
    }

    public void deleteSessionCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, "");

        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false);
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

    public Optional<UUID> findSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
                String sessionId = cookie.getValue();
                return parseUuid(sessionId);
            }
        }

        return Optional.empty();
    }

    private Optional<UUID> parseUuid(String value) {
        try {
            return Optional.of(UUID.fromString(value));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}
