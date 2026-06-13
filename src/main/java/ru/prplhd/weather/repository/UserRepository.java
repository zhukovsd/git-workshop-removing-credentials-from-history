package ru.prplhd.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.prplhd.weather.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByLoginIgnoreCase(String login);

    Optional<UserEntity> findByLoginIgnoreCase(String login);
}
