package ru.prplhd.weather.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.prplhd.weather.dto.openweather.currentweather.WeatherResponseDto;
import ru.prplhd.weather.dto.view.LocationWeatherViewDto;
import ru.prplhd.weather.persistence.entity.LocationEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public interface LocationWeatherViewMapper {

    @Mapping(target = "id", source = "location.id")
    @Mapping(target = "name", source = "location.name")
    @Mapping(target = "country", source = "weather.sys.country")
    @Mapping(target = "icon", source = "weather", qualifiedByName = "extractIcon")
    @Mapping(target = "temp", source = "weather.main.temp", qualifiedByName = "roundTemperature")
    @Mapping(target = "feelsLike", source = "weather.main.feelsLike", qualifiedByName = "roundTemperature")
    @Mapping(target = "description", source = "weather", qualifiedByName = "extractDescription")
    @Mapping(target = "humidity", source = "weather.main.humidity")
    LocationWeatherViewDto toViewDto(LocationEntity location, WeatherResponseDto weather);

    @Named("roundTemperature")
    default Integer roundTemperature(BigDecimal temp) {
        if (temp == null) {
            return null;
        }

        return temp.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    @Named("extractIcon")
    default String extractIcon(WeatherResponseDto weather) {
        if (weather == null || weather.weather() == null || weather.weather().isEmpty()) {
            return null;
        }

        return weather.weather().getFirst().icon();
    }

    @Named("extractDescription")
    default String extractDescription(WeatherResponseDto weather) {
        if (weather == null || weather.weather() == null || weather.weather().isEmpty()) {
            return "—";
        }

        return weather.weather().getFirst().description();
    }
}
