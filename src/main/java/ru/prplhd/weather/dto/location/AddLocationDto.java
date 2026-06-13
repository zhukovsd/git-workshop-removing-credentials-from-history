package ru.prplhd.weather.dto.location;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AddLocationDto(
        @NotBlank(message = "Location name is required")
        @Size(max = 255, message = "Location name must be at most {max} characters")
        String name,

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90", message = "Latitude must be greater than or equal to -90")
        @DecimalMax(value = "90", message = "Latitude must be less than or equal to 90")
        BigDecimal latitude,

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180", message = "Longitude must be greater than or equal to -180")
        @DecimalMax(value = "180", message = "Longitude must be less than or equal to 180")
        BigDecimal longitude
) {
}
