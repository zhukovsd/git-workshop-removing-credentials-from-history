package ru.prplhd.weather.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.prplhd.weather.client.WeatherApiClient;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@Transactional(readOnly = true)
public class UserLocationService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    private final WeatherApiClient weatherApiClient;
    private final ExecutorService weatherApiExecutor;

    private final LocationWeatherViewMapper locationWeatherViewMapper;
    private final LocationMapper locationMapper;

    public UserLocationService(
            LocationRepository locationRepository, UserRepository userRepository,
            WeatherApiClient weatherApiClient, ExecutorService weatherApiExecutor,
            LocationWeatherViewMapper locationWeatherViewMapper, LocationMapper locationMapper
    ) {
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.weatherApiClient = weatherApiClient;
        this.weatherApiExecutor = weatherApiExecutor;
        this.locationWeatherViewMapper = locationWeatherViewMapper;
        this.locationMapper = locationMapper;
    }

    public List<LocationWeatherViewDto> findUserLocationsWithCurrentWeather(Long userId) {
        List<LocationEntity> userLocations = locationRepository.findAllByUserId(userId);

        List<CompletableFuture<LocationWeatherViewDto>> futures = new ArrayList<>();

        for (LocationEntity userLocation : userLocations) {
            BigDecimal latitude = userLocation.getLatitude();
            BigDecimal longitude = userLocation.getLongitude();

            futures.add(
                    CompletableFuture.supplyAsync(() -> {
                                WeatherResponseDto weatherResponseDto = weatherApiClient.getLocationCurrentWeather(latitude, longitude);

                                return locationWeatherViewMapper.toViewDto(
                                        userLocation,
                                        weatherResponseDto
                                );
                            }, weatherApiExecutor
                    )
            );
        }

        List<LocationWeatherViewDto> result = new ArrayList<>();

        for (CompletableFuture<LocationWeatherViewDto> future : futures) {
            result.add(future.join());
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
