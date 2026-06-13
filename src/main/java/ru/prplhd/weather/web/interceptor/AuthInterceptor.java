package ru.prplhd.weather.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.prplhd.weather.dto.auth.AuthenticatedUserDto;
import ru.prplhd.weather.web.auth.AuthenticatedUserProvider;

import java.io.IOException;
import java.util.Optional;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    public AuthInterceptor(AuthenticatedUserProvider authenticatedUserProvider) {
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        Optional<AuthenticatedUserDto> userOpt = authenticatedUserProvider.findAuthenticatedUser(request, response);

        if (userOpt.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/auth/sign-in");
            return false;
        }

        request.setAttribute("authenticatedUserDto", userOpt.get());
        return true;
    }
}
