package ru.prplhd.weather.client;

import ru.prplhd.weather.dto.openweather.currentweather.WeatherResponseDto;
import ru.prplhd.weather.dto.openweather.geocoding.LocationResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface WeatherApiClient {

    List<LocationResponseDto> findLocationsByName(String location);

    WeatherResponseDto getLocationCurrentWeather(BigDecimal latitude, BigDecimal longitude);
}
