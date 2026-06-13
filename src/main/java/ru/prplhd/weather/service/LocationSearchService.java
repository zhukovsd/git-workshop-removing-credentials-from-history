package ru.prplhd.weather.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.prplhd.weather.client.WeatherApiClient;
import ru.prplhd.weather.dto.openweather.geocoding.LocationResponseDto;
import ru.prplhd.weather.dto.view.LocationSearchResultViewDto;
import ru.prplhd.weather.mapper.LocationSearchResultViewMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LocationSearchService {

    private final WeatherApiClient weatherApiClient;
    private final LocationSearchResultViewMapper locationSearchResultViewMapper;

    public LocationSearchService(WeatherApiClient weatherApiClient,
                                 LocationSearchResultViewMapper locationSearchResultViewMapper
    ) {
        this.weatherApiClient = weatherApiClient;
        this.locationSearchResultViewMapper = locationSearchResultViewMapper;
    }

    public List<LocationSearchResultViewDto> searchByName(String name) {
        List<LocationResponseDto> locations = weatherApiClient.findLocationsByName(name);

        List<LocationSearchResultViewDto> result = new ArrayList<>();

        for (LocationResponseDto location : locations) {
            result.add(locationSearchResultViewMapper.toViewDto(location));
        }

        return result;
    }
}
