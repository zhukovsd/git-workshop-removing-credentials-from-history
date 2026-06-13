package ru.prplhd.weather.dto.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SearchLocationDto(
        @NotBlank(message = "Location name is required")
        @Size(max = 255, message = "Location name must be at most {max} characters")
        String name
) {
}
