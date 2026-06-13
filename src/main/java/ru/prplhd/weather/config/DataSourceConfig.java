package ru.prplhd.weather.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:app.properties")
public class DataSourceConfig {

    @Bean(destroyMethod = "close")
    public HikariDataSource dataSource(Environment env) {
        HikariDataSource ds = new HikariDataSource();

        String baseUrl = env.getRequiredProperty("db.url");
        String schema = env.getRequiredProperty("db.schema");
        String jdbcUrl = appendCurrentSchema(baseUrl, schema);

        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(env.getRequiredProperty("db.username"));
        ds.setPassword(env.getRequiredProperty("db.password"));
        ds.setDriverClassName(env.getRequiredProperty("db.driver"));

        int maxPoolSize = env.getRequiredProperty("db.pool.maximum-pool-size", Integer.class);
        ds.setMaximumPoolSize(maxPoolSize);

        int minIdle = env.getRequiredProperty("db.pool.minimum-idle", Integer.class);
        ds.setMinimumIdle(minIdle);

        long connTimeout = env.getRequiredProperty("db.pool.connection-timeout-ms", Long.class);
        ds.setConnectionTimeout(connTimeout);

        long idleTimeout = env.getRequiredProperty("db.pool.idle-timeout-ms", Long.class);
        ds.setIdleTimeout(idleTimeout);

        long maxLifetime = env.getRequiredProperty("db.pool.max-lifetime-ms", Long.class);
        ds.setMaxLifetime(maxLifetime);

        return ds;
    }

    private String appendCurrentSchema(String baseUrl, String schema) {
        String separator = baseUrl.contains("?") ? "&" : "?";
        return baseUrl + separator + "currentSchema=" + schema;
    }
}
