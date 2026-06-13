package ru.prplhd.weather.web.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.prplhd.weather.dto.auth.AuthenticatedUserDto;
import ru.prplhd.weather.web.auth.AuthenticatedUserProvider;

@ControllerAdvice
public class AuthenticatedUserModelAdvice {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    public AuthenticatedUserModelAdvice(AuthenticatedUserProvider authenticatedUserProvider) {
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @ModelAttribute
    public void authenticatedUser(Model model, HttpServletRequest request, HttpServletResponse response) {
        AuthenticatedUserDto user = (AuthenticatedUserDto) request.getAttribute("authenticatedUser");

        if (user != null) {
            model.addAttribute("authenticatedUser", user);
        }
    }
}
