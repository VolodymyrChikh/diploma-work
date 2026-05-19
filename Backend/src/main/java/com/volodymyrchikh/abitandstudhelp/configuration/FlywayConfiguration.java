package com.volodymyrchikh.abitandstudhelp.configuration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.exception.FlywayValidateException;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return (Flyway flyway) -> {
            try {
                flyway.migrate();
            } catch (FlywayValidateException ex) {
                String message = ex.getMessage();
                if (message != null && message.contains("Migration checksum mismatch")) {
                    flyway.repair();
                    flyway.migrate();
                    return;
                }

                throw ex;
            }
        };
    }
}

