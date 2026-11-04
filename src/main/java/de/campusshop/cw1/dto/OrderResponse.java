package de.campusshop.cw1.dto;

import de.campusshop.cw1.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Outbound representation of a stored order.
 */
public record OrderResponse(
        String orderId,
        String externalOrderId,
        String customerId,
        OrderStatus status,
        BigDecimal totalAmount,
        Instant createdAt,
        List<OrderItemResponse> items) {
}
