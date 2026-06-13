package ru.prplhd.weather.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.prplhd.weather.client.WeatherApiClient;
import ru.prplhd.weather.dto.openweather.geocoding.LocationResponseDto;
import ru.prplhd.weather.dto.view.LocationSearchResultViewDto;
import ru.prplhd.weather.mapper.LocationSearchResultViewMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("locationWeather")
public class ExternalLocationSearchServiceTest {

    @Mock
    private WeatherApiClient weatherApiClient;

    private ExternalLocationSearchService externalLocationSearchService;

    @BeforeEach
    void prepareLocationSearchService() {
        LocationSearchResultViewMapper mapper = Mappers.getMapper(LocationSearchResultViewMapper.class);

        externalLocationSearchService = new ExternalLocationSearchService( weatherApiClient, mapper);
    }

    @Test
    @DisplayName("Search by name returns mapped location search results")
    void givenLocationsFound_whenSearchByName_thenReturnsMappedSearchResults() {
        String locationName = "London";

        List<LocationResponseDto> locations = List.of(
                new LocationResponseDto(
                        "London",
                        new BigDecimal("51.5074456"),
                        new BigDecimal("-0.1277653"),
                        "GB",
                        "England"
                ),
                new LocationResponseDto(
                        "City of London",
                        new BigDecimal("51.5156177"),
                        new BigDecimal("-0.0919983"),
                        "GB",
                        "England"
                ),
                new LocationResponseDto(
                        "London",
                        new BigDecimal("42.9832406"),
                        new BigDecimal("-81.2433720"),
                        "CA",
                        "Ontario"
                ),
                new LocationResponseDto(
                        "London",
                        new BigDecimal("37.1283343"),
                        new BigDecimal("-84.0835576"),
                        "US",
                        "Kentucky"
                )
        );

        when(weatherApiClient.findLocationsByName(locationName))
                .thenReturn(locations);

        List<LocationSearchResultViewDto> result = externalLocationSearchService.searchByName(locationName);

        verify(weatherApiClient).findLocationsByName(locationName);

        assertThat(result).hasSize(4);

        LocationSearchResultViewDto secondLocation = result.get(1);

        assertThat(secondLocation.name()).isEqualTo("City of London");
        assertThat(secondLocation.latitude()).isEqualByComparingTo("51.5156177");
        assertThat(secondLocation.longitude()).isEqualByComparingTo("-0.0919983");
        assertThat(secondLocation.country()).isEqualTo("GB");
        assertThat(secondLocation.state()).isEqualTo("England");
    }
}
