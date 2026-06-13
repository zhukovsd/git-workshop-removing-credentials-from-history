package ru.prplhd.weather.mapper;

import org.mapstruct.Mapper;
import ru.prplhd.weather.dto.auth.AuthenticatedUserDto;
import ru.prplhd.weather.persistence.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface AuthenticatedUserMapper {

    AuthenticatedUserDto toAuthenticatedUserDto(UserEntity user);
}
