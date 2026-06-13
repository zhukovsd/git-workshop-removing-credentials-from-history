package ru.prplhd.weather.web.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import ru.prplhd.weather.dto.auth.AuthenticatedUserDto;
import ru.prplhd.weather.service.SessionService;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuthenticatedUserProvider {

    private final SessionCookieManager sessionCookieManager;
    private final SessionService sessionService;

    public AuthenticatedUserProvider(SessionCookieManager sessionCookieManager, SessionService sessionService) {
        this.sessionCookieManager = sessionCookieManager;
        this.sessionService = sessionService;
    }

    public Optional<AuthenticatedUserDto> resolveAuthenticatedUser(HttpServletRequest request, HttpServletResponse response) {
        Optional<UUID> sessionIdOpt = sessionCookieManager.findSessionId(request);

        if (sessionIdOpt.isEmpty()) {

            return Optional.empty();
        }

        UUID sessionId = sessionIdOpt.get();

        Optional<AuthenticatedUserDto> authenticatedUserOpt = sessionService.resolveUserBySessionId(sessionId);

        if (authenticatedUserOpt.isEmpty()) {
            sessionCookieManager.deleteSessionCookie(response);
        }

        return authenticatedUserOpt;
    }
}
