package ru.prplhd.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.prplhd.weather.entity.LocationEntity;

import java.math.BigDecimal;
import java.util.List;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    List<LocationEntity> findAllByUser_Id(Long userId);

    boolean existsByUser_IdAndLatitudeAndLongitude(Long userId, BigDecimal latitude, BigDecimal longitude);

    long deleteByIdAndUser_Id(Long locationId, Long userId);
}
