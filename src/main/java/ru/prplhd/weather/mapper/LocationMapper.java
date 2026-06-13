package ru.prplhd.weather.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.prplhd.weather.dto.location.AddLocationDto;
import ru.prplhd.weather.entity.LocationEntity;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(target = "name", qualifiedByName = "normalizeName")
    @Mapping(target = "user", ignore = true)
    LocationEntity toEntity(AddLocationDto dto);

    @Named("normalizeName")
    default String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            return name;
        }

        return name.trim();
    }
}
