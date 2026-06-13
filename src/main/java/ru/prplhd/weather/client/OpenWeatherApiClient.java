package ru.prplhd.weather.client;

import ru.prplhd.weather.dto.openweather.LocationResponseDto;
import ru.prplhd.weather.exception.OpenWeatherApiException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class OpenWeatherApiClient {

    private static final String GEOCODING_URL_TEMPLATE =
            "%s/geo/1.0/direct?q=%s&limit=%d&appid=%s";

    private static final int GEOCODING_LIMIT = 4;

    private final HttpClient httpClient;
    private final JsonMapper jsonMapper;
    private final String baseUrl;
    private final String apiKey;

    public OpenWeatherApiClient(
            HttpClient httpClient,
            JsonMapper jsonMapper,
            String baseUrl,
            String apiKey
    ) {
        this.httpClient = httpClient;
        this.jsonMapper = jsonMapper;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public List<LocationResponseDto> findLocationsByName(String location) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildGeocodingUri(location))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        String responseBody = sendRequest(request);

        return parseLocations(responseBody);
    }

    private URI buildGeocodingUri(String query) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

        String urlString = GEOCODING_URL_TEMPLATE.formatted(
                baseUrl,
                encodedQuery,
                GEOCODING_LIMIT,
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
        LocationResponseDto[] locations = jsonMapper.readValue(responseBody, LocationResponseDto[].class);

        return List.of(locations);
    }
}
