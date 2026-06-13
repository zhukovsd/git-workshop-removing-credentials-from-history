package ru.prplhd.weather.dto.openweather.currentweather;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record MainDto(
        BigDecimal temp,
        @JsonProperty("feels_like")
        BigDecimal feelsLike,
        Integer humidity
) {
}
