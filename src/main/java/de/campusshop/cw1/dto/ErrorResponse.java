package de.campusshop.cw1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Stable error response shape returned for all failures.
 *
 * @param code      machine-readable error code
 * @param message   human-readable explanation
 * @param timestamp time of failure
 * @param errors    optional field-level validation errors (omitted when empty)
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(String code, String message, Instant timestamp, List<String> errors) {

    public ErrorResponse(String code, String message) {
        this(code, message, Instant.now(), List.of());
    }

    public ErrorResponse(String code, String message, List<String> errors) {
        this(code, message, Instant.now(), errors);
    }
}
