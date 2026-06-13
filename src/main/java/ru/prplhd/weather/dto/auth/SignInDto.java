package ru.prplhd.weather.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record SignInDto(

        @NotBlank(message = "Login is required")
        String login,

        @NotBlank(message = "Password is required")
        String password) {
}
