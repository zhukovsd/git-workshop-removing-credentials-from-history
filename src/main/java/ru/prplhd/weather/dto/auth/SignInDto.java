package ru.prplhd.weather.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignInDto(

        @NotBlank(message = "Login is required")
        @Size(min = 5, max = 255,message = "Login must be between {min} and {max} characters")
        @Pattern(
                regexp = "^[A-Za-z0-9_]+$",
                message = "Login may contain only English letters, digits and underscores"
        )
        String login,

        @NotBlank(message = "Password is required")
        String password) {
}
