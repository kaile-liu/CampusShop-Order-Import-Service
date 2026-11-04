package de.campusshop.cw1.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalised configuration for the file-based order store.
 *
 * @param file path to the JSON file that holds all orders
 */
@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(String file) {
}
