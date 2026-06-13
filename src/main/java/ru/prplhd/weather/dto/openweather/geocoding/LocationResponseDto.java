package ru.prplhd.weather.dto.openweather.geocoding;

import java.math.BigDecimal;

public record LocationResponseDto(
        String name,
        BigDecimal lat,
        BigDecimal lon,
        String country,
        String state
) {
}
