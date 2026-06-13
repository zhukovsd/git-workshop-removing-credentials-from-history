package ru.prplhd.weather.client;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.prplhd.weather.dto.openweather.currentweather.WeatherResponseDto;
import ru.prplhd.weather.dto.openweather.geocoding.LocationResponseDto;
import ru.prplhd.weather.util.CoordinateNormalizer;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Locale;

@Component
@Primary
public class CachingWeatherApiClient implements WeatherApiClient {

    private final WeatherApiClient delegate;
    private final Cache<WeatherCacheKey, WeatherResponseDto> weatherCache;
    private final Cache<LocationCacheKey, List<LocationResponseDto>> locationSearchCache;

    public CachingWeatherApiClient(
            @Qualifier("openWeatherApiClient") WeatherApiClient delegate
    ) {
        this.delegate = delegate;

        this.locationSearchCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(24))
                .maximumSize(1000)
                .build();

        this.weatherCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(5))
                .maximumSize(1000)
                .build();
    }

    @Override
    public List<LocationResponseDto> findLocationsByName(String location) {
        String normalizedLocation = normalizeLocation(location);
        LocationCacheKey locationCacheKey = new LocationCacheKey(normalizedLocation);

        return locationSearchCache.get(
                locationCacheKey,
                ignored -> delegate.findLocationsByName(normalizedLocation)
        );
    }

    @Override
    public WeatherResponseDto getLocationCurrentWeather(BigDecimal latitude, BigDecimal longitude) {
        BigDecimal normalizedLatitude = CoordinateNormalizer.normalize(latitude);
        BigDecimal normalizedLongitude = CoordinateNormalizer.normalize(longitude);

        WeatherCacheKey weatherCacheKey = new WeatherCacheKey(
                normalizedLatitude,
                normalizedLongitude
        );

        return weatherCache.get(
                weatherCacheKey,
                ignored -> delegate.getLocationCurrentWeather(normalizedLatitude, normalizedLongitude)
        );
    }

    private String normalizeLocation(String location) {
        return location.trim().toLowerCase(Locale.ROOT);
    }

    private record WeatherCacheKey(BigDecimal latitude, BigDecimal longitude) {
    }

    private record LocationCacheKey(String location) {
    }
}
