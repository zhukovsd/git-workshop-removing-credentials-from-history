package ru.prplhd.weather.service;

import org.springframework.stereotype.Service;
import ru.prplhd.weather.client.OpenWeatherApiClient;
import ru.prplhd.weather.dto.openweather.LocationResponseDto;

import java.util.List;

@Service
public class LocationSearchService {

    private final OpenWeatherApiClient openWeatherApiClient;

    public LocationSearchService(OpenWeatherApiClient openWeatherApiClient) {
        this.openWeatherApiClient = openWeatherApiClient;
    }

    public List<LocationResponseDto> searchByName(String name) {
        return openWeatherApiClient.findLocationsByName(name);
    }
}
