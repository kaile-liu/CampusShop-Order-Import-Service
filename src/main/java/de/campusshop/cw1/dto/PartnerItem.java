package de.campusshop.cw1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * A single line item of the inbound partner payload.
 */
public record PartnerItem(
        @NotBlank String sku,
        String name,
        @Min(1) int quantity,
        @NotNull @PositiveOrZero BigDecimal unitPrice) {
}
