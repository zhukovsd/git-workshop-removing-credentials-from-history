package ru.prplhd.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import ru.prplhd.weather.client.OpenWeatherApiClient;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@PropertySource("classpath:app-test.properties")
public class OpenWeatherApiClientTestConfig {

    @Bean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Bean
    public OpenWeatherApiClient openWeatherApiClient(
            HttpClient httpClient,
            JsonMapper jsonMapper,
            Environment env
    ) {
        return new OpenWeatherApiClient(
                httpClient,
                jsonMapper,
                env.getRequiredProperty("openweather.base-url"),
                env.getRequiredProperty("openweather.location-search-limit", Integer.class),
                env.getRequiredProperty("openweather.api-key")
        );
    }
}
