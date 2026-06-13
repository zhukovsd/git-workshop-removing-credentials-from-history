package ru.prplhd.weather.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.prplhd.weather.persistence.entity.LocationEntity;

import java.util.List;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    List<LocationEntity> findAllByUserId(Long userId);
}
