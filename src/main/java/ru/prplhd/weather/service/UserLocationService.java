package ru.prplhd.weather.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.prplhd.weather.client.OpenWeatherApiClient;
import ru.prplhd.weather.dto.location.AddLocationDto;
import ru.prplhd.weather.dto.openweather.currentweather.WeatherResponseDto;
import ru.prplhd.weather.dto.view.LocationWeatherViewDto;
import ru.prplhd.weather.exception.location.LocationAlreadyExistsException;
import ru.prplhd.weather.exception.location.LocationNotFoundException;
import ru.prplhd.weather.mapper.LocationMapper;
import ru.prplhd.weather.mapper.LocationWeatherViewMapper;
import ru.prplhd.weather.persistence.entity.LocationEntity;
import ru.prplhd.weather.persistence.entity.UserEntity;
import ru.prplhd.weather.persistence.repository.LocationRepository;
import ru.prplhd.weather.persistence.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserLocationService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    private final OpenWeatherApiClient openWeatherApiClient;

    private final LocationWeatherViewMapper locationWeatherViewMapper;
    private final LocationMapper locationMapper;

    public UserLocationService(
            LocationRepository locationRepository, UserRepository userRepository,
            OpenWeatherApiClient openWeatherApiClient,
            LocationWeatherViewMapper locationWeatherViewMapper, LocationMapper locationMapper
    ) {
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.openWeatherApiClient = openWeatherApiClient;
        this.locationWeatherViewMapper = locationWeatherViewMapper;
        this.locationMapper = locationMapper;
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

    @Transactional
    public void saveLocation(Long userId, AddLocationDto addLocationDto) {
        boolean locationAlreadyExists = locationRepository.existsByUser_IdAndLatitudeAndLongitude(
                userId,
                addLocationDto.latitude().setScale(6, RoundingMode.HALF_UP),
                addLocationDto.longitude().setScale(6, RoundingMode.HALF_UP)
        );

        if (locationAlreadyExists) {
            throw new LocationAlreadyExistsException("Location already exists for current user");
        }

        LocationEntity locationEntity = locationMapper.toEntity(addLocationDto);

        UserEntity userReference = userRepository.getReferenceById(userId);
        locationEntity.setUser(userReference);

        locationRepository.save(locationEntity);
    }

    @Transactional
    public void deleteLocation(Long locationId, Long userId) {
        long deletedLocations = locationRepository.deleteByIdAndUser_Id(locationId, userId);

        if (deletedLocations == 0) {
            throw new LocationNotFoundException("Location does not exist or does not belong to current user");
        }
    }
}
