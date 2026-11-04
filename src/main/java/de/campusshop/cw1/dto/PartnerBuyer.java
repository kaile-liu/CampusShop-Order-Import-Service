package de.campusshop.cw1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Buyer block of the inbound partner payload.
 */
public record PartnerBuyer(
        @NotBlank String id,
        @NotBlank String fullName,
        @NotBlank @Email String email) {
}
