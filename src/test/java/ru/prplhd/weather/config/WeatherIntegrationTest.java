package ru.prplhd.weather.config;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringJUnitConfig(classes = {
        AppConfig.class,
        DataSourceConfig.class,
        JpaConfig.class,
        LiquibaseConfig.class
})
@TestPropertySource("classpath:app-test.properties")
@Transactional
public @interface WeatherIntegrationTest {
}
