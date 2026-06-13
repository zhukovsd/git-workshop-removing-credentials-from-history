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
import ru.prplhd.weather.dto.openweather.currentweather.WeatherResponseDto;
import ru.prplhd.weather.dto.openweather.geocoding.LocationResponseDto;
import ru.prplhd.weather.exception.openweather.OpenWeatherApiException;

import java.math.BigDecimal;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

        assertThat(result.getLast().name()).isEqualTo("London");
        assertThat(result.getLast().lat()).isEqualByComparingTo("37.1283343");
        assertThat(result.getLast().lon()).isEqualByComparingTo("-84.0835576");
        assertThat(result.getLast().country()).isEqualTo("US");
        assertThat(result.getLast().state()).isEqualTo("Kentucky");
    }

    @Test
    @DisplayName("Throws exception when OpenWeather geocoding API responds with 401")
    void whenFindLocationsByName_withUnauthorizedApiResponse_thenThrowsOpenWeatherApiException() {
        stubFor(get(urlPathEqualTo("/geo/1.0/direct"))
                .withQueryParam("q", equalTo("London"))
                .withQueryParam("limit", equalTo(locationSearchLimit))
                .withQueryParam("appid", equalTo(apiKey))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                """
                                {
                                  "cod": 401,
                                  "message": "Invalid API key"
                                }
                                """
                        )
                )
        );

        assertThatThrownBy(() -> client.findLocationsByName("London"))
                .isInstanceOf(OpenWeatherApiException.class)
                .hasMessageContaining("401");
    }

    @Test
    @DisplayName("Returns empty list when OpenWeather geocoding API returns empty array")
    void whenFindLocationsByName_withEmptyApiResponse_thenReturnsEmptyList() {
        stubFor(get(urlPathEqualTo("/geo/1.0/direct"))
                .withQueryParam("q", equalTo("London"))
                .withQueryParam("limit", equalTo(locationSearchLimit))
                .withQueryParam("appid", equalTo(apiKey))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                """
                                []
                                """
                        )
                )
        );

        List<LocationResponseDto> result = client.findLocationsByName("London");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Returns parsed weather when OpenWeather current weather API responds successfully")
    void whenGetLocationCurrentWeather_withSuccessfulApiResponse_thenReturnsParsedWeather() {
        stubFor(get(urlPathEqualTo("/data/2.5/weather"))
                .withQueryParam("lat", equalTo("54.6295687"))
                .withQueryParam("lon", equalTo("39.7425039"))
                .withQueryParam("appid", equalTo(apiKey))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                """
                                {
                                  "coord": {
                                    "lon": 30.3162,
                                    "lat": 54.6296
                                  },
                                  "weather": [
                                    {
                                      "description": "overcast clouds",
                                      "icon": "04d"
                                    }
                                  ],
                                  "main": {
                                    "temp": 292.46,
                                    "feels_like": 292.38,
                                    "humidity": 74
                                  },
                                  "sys": {
                                    "country": "BY"
                                  }
                                }
                                """
                        )
                )
        );

        WeatherResponseDto result = client.getLocationCurrentWeather(BigDecimal.valueOf(54.6295687), BigDecimal.valueOf(39.7425039));

        assertThat(result.coord().lat()).isEqualByComparingTo("54.6296");
        assertThat(result.coord().lon()).isEqualByComparingTo("30.3162");

        assertThat(result.main().temp()).isEqualByComparingTo("292.46");
        assertThat(result.main().feelsLike()).isEqualByComparingTo("292.38");
        assertThat(result.main().humidity()).isEqualTo(74);

        assertThat(result.sys().country()).isEqualTo("BY");

        assertThat(result.weather()).hasSize(1);
        assertThat(result.weather().getFirst().description()).isEqualTo("overcast clouds");
        assertThat(result.weather().getFirst().icon()).isEqualTo("04d");
    }
}
