package de.campusshop.cw1;

import de.campusshop.cw1.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * CampusShop DataHub — Coursework 1 reference solution.
 *
 * <p>A single Spring Boot REST service that imports partner orders, transforms them into the
 * internal domain model and persists them to a local JSON file.
 */
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Cw1Application {

    public static void main(String[] args) {
        SpringApplication.run(Cw1Application.class, args);
    }
}
