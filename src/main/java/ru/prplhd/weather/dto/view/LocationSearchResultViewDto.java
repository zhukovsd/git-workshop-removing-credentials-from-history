package ru.prplhd.weather.dto.view;

import java.math.BigDecimal;

public record LocationSearchResultViewDto(
        String name,
        BigDecimal latitude,
        BigDecimal longitude,
        String country,
        String state
) {
}
