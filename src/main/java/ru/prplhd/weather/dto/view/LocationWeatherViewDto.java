package ru.prplhd.weather.dto.view;

public record LocationWeatherViewDto(
        Long id,
        String name,
        String country,
        String icon,
        Integer temp,
        Integer feelsLike,
        String description,
        Integer humidity
) {
}
