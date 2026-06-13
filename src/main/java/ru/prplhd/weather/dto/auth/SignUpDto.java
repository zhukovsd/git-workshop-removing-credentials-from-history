package ru.prplhd.weather.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpDto(
        @NotBlank(message = "Login is required")
        @Size(min = 5, max = 255,message = "Login must be between {min} and {max} characters")
        @Pattern(
                regexp = "^[A-Za-z0-9_]+$",
                message = "Login may contain only English letters, digits and underscores"
        )
        String login,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 72, message = "Password must be between {min} and {max} characters")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)[\\x21-\\x7E]+$",
                message = "Password may contain letters, digits and symbols (at least one letter and one digit)"
        )
        String password,

        String confirmPassword){
}
