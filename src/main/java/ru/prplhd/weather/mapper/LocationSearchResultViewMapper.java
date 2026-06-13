package ru.prplhd.weather.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.prplhd.weather.dto.openweather.geocoding.LocationResponseDto;
import ru.prplhd.weather.dto.view.LocationSearchResultViewDto;

@Mapper(componentModel = "spring")
public interface LocationSearchResultViewMapper {

    @Mapping(target = "latitude", source = "location.lat")
    @Mapping(target = "longitude", source = "location.lon")
    LocationSearchResultViewDto toViewDto(LocationResponseDto location);
}
