package ru.prplhd.weather.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.prplhd.weather.client.WeatherApiClient;
import ru.prplhd.weather.config.AppConfig;
import ru.prplhd.weather.config.DataSourceConfig;
import ru.prplhd.weather.config.JpaConfig;
import ru.prplhd.weather.config.LiquibaseConfig;
import ru.prplhd.weather.dto.openweather.currentweather.*;
import ru.prplhd.weather.dto.view.LocationWeatherViewDto;
import ru.prplhd.weather.entity.LocationEntity;
import ru.prplhd.weather.entity.UserEntity;
import ru.prplhd.weather.repository.LocationRepository;
import ru.prplhd.weather.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {
        AppConfig.class,
        DataSourceConfig.class,
        JpaConfig.class,
        LiquibaseConfig.class
})
@TestPropertySource("classpath:app-test.properties")
@Transactional
@Tag("locationWeather")
public class UserLocationServiceTest {

    @MockitoBean(enforceOverride = true)
    private WeatherApiClient weatherApiClient;

    @Autowired
    private UserLocationService userLocationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Test
    @DisplayName("Find locations with weather returns mapped weather views")
    void givenSavedLocations_whenFindLocationsWithWeather_thenReturnsMappedWeatherViews() {
        UserEntity user = createUserWithLondonAndMoscowLocations();
        Long userId = user.getId();

        BigDecimal londonLon = new BigDecimal("-0.127653");
        BigDecimal londonLan = new BigDecimal("51.507456");

        WeatherResponseDto londonWeatherResponse = new WeatherResponseDto(
                new CoordDto(
                        londonLon,
                        londonLan
                ),
                List.of(
                        new WeatherConditionDto(
                                "broken clouds",
                                "04d"
                        )
                ),
                new MainDto(
                        new BigDecimal("300.41"),
                        new BigDecimal("300.50"),
                        45
                ),
                new SysDto("GB")
        );

        BigDecimal moscowLon = new BigDecimal("37.617482");
        BigDecimal moscowLan = new BigDecimal("55.750412");

        WeatherResponseDto moscowWeatherResponse = new WeatherResponseDto(
                new CoordDto(
                        moscowLon,
                        moscowLan
                ),
                List.of(
                        new WeatherConditionDto(
                                "clear sky",
                                "01d"
                        )
                ),
                new MainDto(
                        new BigDecimal("303.38"),
                        new BigDecimal("302.46"),
                        34
                ),
                new SysDto("RU")
        );

        when(weatherApiClient.getLocationCurrentWeather(londonLan, londonLon))
                .thenReturn(londonWeatherResponse);

        when(weatherApiClient.getLocationCurrentWeather(moscowLan, moscowLon))
                .thenReturn(moscowWeatherResponse);

        List<LocationWeatherViewDto> result = userLocationService.findUserLocationsWithCurrentWeather(userId);

        verify(weatherApiClient).getLocationCurrentWeather(londonLan, londonLon);
        verify(weatherApiClient).getLocationCurrentWeather(moscowLan, moscowLon);

        assertThat(result).hasSize(2);

        LocationWeatherViewDto londonWeather = result.stream()
                .filter(location -> location.name().equals("London"))
                .findFirst()
                .orElseThrow();

        assertThat(londonWeather.id()).isNotNull();
        assertThat(londonWeather.name()).isEqualTo("London");
        assertThat(londonWeather.country()).isEqualTo("GB");
        assertThat(londonWeather.icon()).isEqualTo("04d");
        assertThat(londonWeather.temp()).isEqualTo(300);
        assertThat(londonWeather.feelsLike()).isEqualTo(301);
        assertThat(londonWeather.description()).isEqualTo("broken clouds");
        assertThat(londonWeather.humidity()).isEqualTo(45);


        LocationWeatherViewDto moscowWeather = result.stream()
                .filter(location -> location.name().equals("Moscow"))
                .findFirst()
                .orElseThrow();

        assertThat(moscowWeather.id()).isNotNull();
        assertThat(moscowWeather.name()).isEqualTo("Moscow");
        assertThat(moscowWeather.country()).isEqualTo("RU");
        assertThat(moscowWeather.icon()).isEqualTo("01d");
        assertThat(moscowWeather.temp()).isEqualTo(303);
        assertThat(moscowWeather.feelsLike()).isEqualTo(302);
        assertThat(moscowWeather.description()).isEqualTo("clear sky");
        assertThat(moscowWeather.humidity()).isEqualTo(34);
    }

    private UserEntity createUserWithLondonAndMoscowLocations() {
        UserEntity user = new UserEntity("test-user", "password-hash");

        UserEntity savedUser = userRepository.save(user);

        LocationEntity london = new LocationEntity(
                "London",
                user,
                new BigDecimal("51.507456"),
                new BigDecimal("-0.127653")
        );

        LocationEntity moscow = new LocationEntity(
                "Moscow",
                user,
                new BigDecimal("55.750412"),
                new BigDecimal("37.617482")
        );

        locationRepository.saveAll(List.of(london, moscow));

        return savedUser;
    }
}
