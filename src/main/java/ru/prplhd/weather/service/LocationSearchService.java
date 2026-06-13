package ru.prplhd.weather.service;

import org.springframework.stereotype.Service;
import ru.prplhd.weather.client.OpenWeatherApiClient;
import ru.prplhd.weather.dto.openweather.geocoding.LocationResponseDto;
import ru.prplhd.weather.dto.view.LocationSearchResultViewDto;
import ru.prplhd.weather.mapper.LocationSearchResultViewMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationSearchService {

    private final OpenWeatherApiClient openWeatherApiClient;
    private final LocationSearchResultViewMapper locationSearchResultViewMapper;

    public LocationSearchService(OpenWeatherApiClient openWeatherApiClient,
                                 LocationSearchResultViewMapper locationSearchResultViewMapper
    ) {
        this.openWeatherApiClient = openWeatherApiClient;
        this.locationSearchResultViewMapper = locationSearchResultViewMapper;
    }

    public List<LocationSearchResultViewDto> searchByName(String name) {
        List<LocationResponseDto> locations = openWeatherApiClient.findLocationsByName(name);

        List<LocationSearchResultViewDto> result = new ArrayList<>();

        for (LocationResponseDto location : locations) {
            result.add(locationSearchResultViewMapper.toViewDto(location));
        }

        return result;
    }
}
