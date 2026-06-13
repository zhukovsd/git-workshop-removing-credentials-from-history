package ru.prplhd.weather.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.prplhd.weather.entity.LocationEntity;
import ru.prplhd.weather.repository.LocationRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserLocationReader {

    private final LocationRepository locationRepository;

    public List<LocationEntity> findUserLocations(Long userId) {
        return locationRepository.findAllByUser_Id(userId);
    }
}
