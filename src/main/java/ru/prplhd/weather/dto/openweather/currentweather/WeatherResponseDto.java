package ru.prplhd.weather.dto.openweather.currentweather;

import java.util.List;

public record WeatherResponseDto(
        CoordDto coord,
        List<WeatherConditionDto> weather,
        MainDto main,
        SysDto sys
) {
}
