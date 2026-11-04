package de.campusshop.cw1.dto;

import java.math.BigDecimal;

/**
 * Outbound representation of an order line item.
 */
public record OrderItemResponse(String sku, String name, int quantity, BigDecimal unitPrice) {
}
