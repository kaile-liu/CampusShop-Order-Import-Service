package de.campusshop.cw1.mapper;

import de.campusshop.cw1.dto.OrderItemResponse;
import de.campusshop.cw1.dto.OrderResponse;
import de.campusshop.cw1.dto.PartnerOrderRequest;
import de.campusshop.cw1.model.Order;
import de.campusshop.cw1.model.OrderItem;
import de.campusshop.cw1.model.OrderStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Manual mapper that keeps transformation logic out of the controller and service.
 */
@Component
public class OrderMapper {

    /**
     * Build a new internal {@link Order} from a validated partner request:
     * copy {@code partnerOrderId} → {@code externalOrderId} and {@code buyer.id} → {@code customerId},
     * map each item, compute {@code totalAmount} as the sum of {@code quantity * unitPrice},
     * set status {@code IMPORTED} and use the supplied {@code orderId}/{@code createdAt}.
     */
    public Order toDomain(PartnerOrderRequest request, String orderId, Instant createdAt) {
        return new Order(
                orderId,
                request.partnerOrderId(),
                request.buyer().id(),
                OrderStatus.IMPORTED,
                request.items().stream()
                        .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                createdAt,
                request.items().stream()
                        .map(item -> new OrderItem(item.sku(), item.name(), item.quantity(), item.unitPrice()))
                        .toList()
        );
    }

    /** Map an internal {@link Order} to its API response DTO. */
    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.orderId(),
                order.externalOrderId(),
                order.customerId(),
                order.status(),
                order.totalAmount(),
                order.createdAt(),
                order.items().stream()
                        .map(item -> new OrderItemResponse(item.sku(), item.name(), item.quantity(), item.unitPrice()))
                        .toList()
        );
    }
}
