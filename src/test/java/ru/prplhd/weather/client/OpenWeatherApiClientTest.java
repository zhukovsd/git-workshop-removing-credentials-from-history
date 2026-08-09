package ru.prplhd.weather.client;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.prplhd.weather.config.OpenWeatherApiClientTestConfig;
import ru.prplhd.weather.dto.openweather.geocoding.LocationResponseDto;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = OpenWeatherApiClientTestConfig.class)
@TestPropertySource("classpath:app-test.properties")
@WireMockTest(httpPort = 13377)
@Tag("httpServer")
public class OpenWeatherApiClientTest {

    @Autowired
    private OpenWeatherApiClient client;

    @Value("${openweather.api-key}")
    private String apiKey;

    @Value("${openweather.location-search-limit}")
    private String locationSearchLimit;

    @Test
    @DisplayName("Returns parsed locations when OpenWeather geocoding API responds successfully")
    void whenFindLocationsByName_withSuccessfulApiResponse_thenReturnsParsedLocations() {
        stubFor(get(urlPathEqualTo("/geo/1.0/direct"))
                .withQueryParam("q", equalTo("London"))
                .withQueryParam("limit", equalTo(locationSearchLimit))
                .withQueryParam("appid", equalTo(apiKey))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                """
                                [
                                  {
                                    "name": "London",
                                    "lat": 51.5074456,
                                    "lon": -0.1277653,
                                    "country": "GB",
                                    "state": "England"
                                  },
                                  {
                                    "name": "City of London",
                                    "lat": 51.5156177,
                                    "lon": -0.0919983,
                                    "country": "GB",
                                    "state": "England"
                                  },
                                  {
                                    "name": "London",
                                    "lat": 42.9832406,
                                    "lon": -81.243372,
                                    "country": "CA",
                                    "state": "Ontario"
                                  },
                                  {
                                    "name": "London",
                                    "lat": 37.1283343,
                                    "lon": -84.0835576,
                                    "country": "US",
                                    "state": "Kentucky"
                                  }
                                ]
                                """
                        )
                )
        );

        List<LocationResponseDto> result = client.findLocationsByName("London");

        assertThat(result).hasSize(4);

        assertThat(result.getFirst().name()).isEqualTo("London");

        assertThat(result.getFirst().lat()).isEqualByComparingTo("51.5074456");
        assertThat(result.getFirst().lon()).isEqualByComparingTo("-0.1277653");

        assertThat(result.getFirst().country()).isEqualTo("GB");
        assertThat(result.getFirst().state()).isEqualTo("England");
    }
}
