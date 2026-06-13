package ru.prplhd.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource("classpath:app.properties")
@EnableTransactionManagement
@EnableJpaRepositories("ru.prplhd.weather.repository")
public class JpaConfig {

    private static final String[] HIBERNATE_PROPERTY_NAMES = {
            "hibernate.dialect",
            "hibernate.show-sql",
            "hibernate.default-schema",
            "hibernate.hbm2ddl.auto"
    };
    private static final String PACKAGE_TO_SCAN = "ru.prplhd.weather.entity";

    private final Environment env;
    private final DataSource dataSource;

    public JpaConfig(Environment env, DataSource dataSource) {
        this.env = env;
        this.dataSource = dataSource;
    }

    @Bean
    @DependsOn("liquibase")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setPackagesToScan(PACKAGE_TO_SCAN);

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
        entityManagerFactory.setJpaProperties(hibernateProperties());

        return entityManagerFactory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

        return transactionManager;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();

        for (String propertyName : HIBERNATE_PROPERTY_NAMES) {
            properties.put(propertyName, env.getRequiredProperty(propertyName));
        }

        return properties;
    }
}
