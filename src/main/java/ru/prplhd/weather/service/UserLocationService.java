package ru.prplhd.weather.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.prplhd.weather.client.WeatherApiClient;
import ru.prplhd.weather.dto.location.AddLocationDto;
import ru.prplhd.weather.dto.openweather.currentweather.WeatherResponseDto;
import ru.prplhd.weather.dto.view.LocationWeatherViewDto;
import ru.prplhd.weather.entity.LocationEntity;
import ru.prplhd.weather.entity.UserEntity;
import ru.prplhd.weather.exception.location.LocationAlreadyExistsException;
import ru.prplhd.weather.exception.location.LocationNotFoundException;
import ru.prplhd.weather.exception.openweather.OpenWeatherApiException;
import ru.prplhd.weather.mapper.LocationMapper;
import ru.prplhd.weather.mapper.LocationWeatherViewMapper;
import ru.prplhd.weather.repository.LocationRepository;
import ru.prplhd.weather.repository.UserRepository;
import ru.prplhd.weather.util.CoordinateNormalizer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class UserLocationService {

    private final UserLocationReader userLocationReader;

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    private final WeatherApiClient weatherApiClient;
    private final ExecutorService weatherApiExecutor;

    private final LocationWeatherViewMapper locationWeatherViewMapper;
    private final LocationMapper locationMapper;

    public List<LocationWeatherViewDto> findUserLocationsWithCurrentWeather(Long userId) {
        List<LocationEntity> userLocations = userLocationReader.findUserLocations(userId);

        return enrichLocationsWithWeather(userLocations);
    }

    @Transactional
    public void saveLocation(Long userId, AddLocationDto addLocationDto) {
        boolean locationAlreadyExists = locationRepository.existsByUser_IdAndLatitudeAndLongitude(
                userId,
                CoordinateNormalizer.normalize(addLocationDto.latitude()),
                CoordinateNormalizer.normalize(addLocationDto.longitude())
        );

        if (locationAlreadyExists) {
            throw new LocationAlreadyExistsException("Location already exists for current user");
        }

        UserEntity userReference = userRepository.getReferenceById(userId);
        LocationEntity locationEntity = locationMapper.toEntity(addLocationDto, userReference);

        locationRepository.save(locationEntity);
    }

    @Transactional
    public void deleteLocation(Long locationId, Long userId) {
        long deletedLocations = locationRepository.deleteByIdAndUser_Id(locationId, userId);

        if (deletedLocations == 0) {
            throw new LocationNotFoundException("Location does not exist or does not belong to current user");
        }
    }

    private List<LocationWeatherViewDto> enrichLocationsWithWeather(List<LocationEntity> userLocations) {
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
            try {
                result.add(future.join());
            } catch (CompletionException e) {

                if (e.getCause() instanceof OpenWeatherApiException openWeatherException) {
                    throw  openWeatherException;
                }

                throw new IllegalStateException("Unexpected error while loading weather", e.getCause());
            }
        }

        return result;
    }
}
