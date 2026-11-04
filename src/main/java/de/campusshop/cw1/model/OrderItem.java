package de.campusshop.cw1.model;

import java.math.BigDecimal;

/**
 * A single line item of an order. Immutable domain record.
 */
public record OrderItem(String sku, String name, int quantity, BigDecimal unitPrice) {

    /** Line total = unitPrice * quantity. */
    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
