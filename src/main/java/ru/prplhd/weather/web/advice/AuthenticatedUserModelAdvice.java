package ru.prplhd.weather.web.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestAttribute;
import ru.prplhd.weather.dto.auth.AuthenticatedUserDto;

@ControllerAdvice
public class AuthenticatedUserModelAdvice {

    @ModelAttribute
    public void authenticatedUser(
            @RequestAttribute(name = "authenticatedUserDto", required = false) AuthenticatedUserDto user,
            Model model) {

        if (user != null) {
            model.addAttribute("authenticatedUserDto", user);
        }
    }
}
