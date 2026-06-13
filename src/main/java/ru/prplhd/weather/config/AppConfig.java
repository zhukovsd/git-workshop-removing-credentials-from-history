package ru.prplhd.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.prplhd.weather.client.OpenWeatherApiClient;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.net.http.HttpClient;
import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@PropertySource("classpath:app.properties")
@EnableScheduling
@ComponentScan({
        "ru.prplhd.weather.service",
        "ru.prplhd.weather.util",
        "ru.prplhd.weather.mapper",
        "ru.prplhd.weather.scheduler",
        "ru.prplhd.weather.client"
})
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public Duration sessionTtl(Environment env) {
        long ttlHours = env.getRequiredProperty("app.session.ttl-hours", Long.class);

        return Duration.ofHours(ttlHours);
    }

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

    @Bean(destroyMethod = "shutdown")
    public ExecutorService weatherApiExecutor() {
        return Executors.newFixedThreadPool(8);
    }
}
