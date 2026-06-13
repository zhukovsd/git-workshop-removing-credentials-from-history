package ru.prplhd.weather.client;

import lombok.RequiredArgsConstructor;
import ru.prplhd.weather.dto.openweather.currentweather.WeatherResponseDto;
import ru.prplhd.weather.dto.openweather.geocoding.LocationResponseDto;
import ru.prplhd.weather.exception.openweather.OpenWeatherApiException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
public class OpenWeatherApiClient implements WeatherApiClient{

    private static final String GEOCODING_URL_TEMPLATE =
            "%s/geo/1.0/direct?q=%s&limit=%d&appid=%s";

    private static final String CURRENT_WEATHER_URL_TEMPLATE =
            "%s/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=metric";

    private final HttpClient httpClient;
    private final JsonMapper jsonMapper;

    private final String baseUrl;
    private final int limit;
    private final String apiKey;

    @Override
    public List<LocationResponseDto> findLocationsByName(String location) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildGeocodingUri(location))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        String responseBody = sendRequest(request);

        return parseLocations(responseBody);
    }

    @Override
    public WeatherResponseDto getLocationCurrentWeather(BigDecimal latitude, BigDecimal longitude) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildCurrentWeatherUri(latitude, longitude))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        String responseBody = sendRequest(request);

        return jsonMapper.readValue(responseBody, WeatherResponseDto.class);
    }

    private URI buildGeocodingUri(String query) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

        String urlString = GEOCODING_URL_TEMPLATE.formatted(
                baseUrl,
                encodedQuery,
                limit,
                apiKey
        );

        return URI.create(urlString);
    }

    private URI buildCurrentWeatherUri(BigDecimal latitude, BigDecimal longitude) {
        String urlString = CURRENT_WEATHER_URL_TEMPLATE.formatted(
                baseUrl,
                latitude.toPlainString(),
                longitude.toPlainString(),
                apiKey
        );

        return URI.create(urlString);
    }

    private String sendRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new OpenWeatherApiException(
                        "OpenWeather API returned status: " + response.statusCode()
                );
            }

            return response.body();

        } catch (IOException e) {
            throw new OpenWeatherApiException("Failed to call OpenWeather API", e);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OpenWeatherApiException("OpenWeather API request was interrupted", e);
        }
    }

    private List<LocationResponseDto> parseLocations(String responseBody) {
        LocationResponseDto[] locations;

        try {
            locations = jsonMapper.readValue(responseBody, LocationResponseDto[].class);
        } catch (JacksonException e) {
            throw new OpenWeatherApiException("Failed to parse locations response", e);
        }

        return List.of(locations);
    }
}
