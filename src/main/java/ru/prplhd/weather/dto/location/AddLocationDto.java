package ru.prplhd.weather.dto.location;

import java.math.BigDecimal;

public record AddLocationDto(
        String name,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
