package ru.prplhd.weather.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.prplhd.weather.dto.location.AddLocationDto;
import ru.prplhd.weather.persistence.entity.LocationEntity;

import java.util.Locale;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(target = "name", qualifiedByName = "normalizeName")
    LocationEntity toEntity(AddLocationDto dto);

    @Named("normalizeName")
    default String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            return name;
        }

        String trimmed = name.trim().toLowerCase(Locale.ROOT);

        return trimmed.substring(0, 1).toUpperCase(Locale.ROOT) + trimmed.substring(1);
    }
}
