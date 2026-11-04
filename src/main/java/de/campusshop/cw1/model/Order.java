package de.campusshop.cw1.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Internal order domain model. This is deliberately different from the external
 * partner JSON shape (Week 1: "domain models should represent internal meaning,
 * not external JSON structure").
 */
public record Order(
        String orderId,
        String externalOrderId,
        String customerId,
        OrderStatus status,
        BigDecimal totalAmount,
        Instant createdAt,
        List<OrderItem> items) {
}
