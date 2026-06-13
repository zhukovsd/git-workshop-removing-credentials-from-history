package ru.prplhd.weather.dto.openweather.currentweather;

import java.math.BigDecimal;

public record CoordDto(
        BigDecimal lon,
        BigDecimal lat
) {
}
