package de.campusshop.cw1.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Inbound partner order payload for {@code POST /partner-orders}.
 */
public record PartnerOrderRequest(
        @NotBlank String partnerOrderId,
        @NotNull @Valid PartnerBuyer buyer,
        @NotEmpty @Valid List<PartnerItem> items,
        String sourceSystem) {
}
