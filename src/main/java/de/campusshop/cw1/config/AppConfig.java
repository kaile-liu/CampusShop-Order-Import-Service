package de.campusshop.cw1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AppConfig {

    /** A UTC clock, injected into the service so timestamps are testable. */
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
