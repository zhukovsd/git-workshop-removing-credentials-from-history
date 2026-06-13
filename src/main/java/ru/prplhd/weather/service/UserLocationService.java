package ru.prplhd.weather.service;

import org.springframework.stereotype.Service;
import ru.prplhd.weather.client.OpenWeatherApiClient;
import ru.prplhd.weather.dto.openweather.currentweather.WeatherResponseDto;
import ru.prplhd.weather.dto.view.LocationWeatherViewDto;
import ru.prplhd.weather.mapper.LocationWeatherViewMapper;
import ru.prplhd.weather.persistence.entity.LocationEntity;
import ru.prplhd.weather.persistence.repository.LocationRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserLocationService {

    private final LocationRepository locationRepository;
    private final OpenWeatherApiClient openWeatherApiClient;
    private final LocationWeatherViewMapper locationWeatherViewMapper;

    public UserLocationService(
            LocationRepository locationRepository,
            OpenWeatherApiClient openWeatherApiClient,
            LocationWeatherViewMapper locationWeatherViewMapper
    ) {
        this.locationRepository = locationRepository;
        this.openWeatherApiClient = openWeatherApiClient;
        this.locationWeatherViewMapper = locationWeatherViewMapper;
    }

    public List<LocationWeatherViewDto> findUserLocationsWithCurrentWeather(Long userId) {
        List<LocationEntity> userLocations = locationRepository.findAllByUserId(userId);

        List<LocationWeatherViewDto> result = new ArrayList<>();

        for (LocationEntity userLocation : userLocations) {
            BigDecimal latitude = userLocation.getLatitude();
            BigDecimal longitude = userLocation.getLongitude();

            WeatherResponseDto weatherResponseDto = openWeatherApiClient.getLocationCurrentWeather(
                    latitude,
                    longitude
            );

            LocationWeatherViewDto viewDto = locationWeatherViewMapper.toViewDto(
                    userLocation,
                    weatherResponseDto
            );

            result.add(viewDto);
        }

        return result;
    }
}
