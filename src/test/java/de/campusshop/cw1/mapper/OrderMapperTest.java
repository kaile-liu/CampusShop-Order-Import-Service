package de.campusshop.cw1.mapper;

import de.campusshop.cw1.dto.*;
import de.campusshop.cw1.model.Order;
import de.campusshop.cw1.model.OrderItem;
import de.campusshop.cw1.model.OrderStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link OrderMapper}.
 * test that a partner request maps to an {@code Order} with the external/customer
 * ids copied across, status {@code IMPORTED}, and {@code totalAmount} = Σ {@code quantity*unitPrice};
 * and that {@code toResponse} echoes the items.
 */
class OrderMapperTest {

    private final OrderMapper mapper = new OrderMapper();

    @Test
    void toDomainComputesTotalAndCopiesIds() {
        // arrange a PartnerOrderRequest, act with mapper.toDomain(...), assert with AssertJ
        PartnerOrderRequest request = new PartnerOrderRequest(
                "partner-123",
                new PartnerBuyer("customer-456", "Alice Smith", "alice@example.com"),
                List.of(
                        new PartnerItem("SKU-1", "Keyboard", 2, new BigDecimal("10.00")),
                        new PartnerItem("SKU-2", "Mouse", 3, new BigDecimal("5.00"))
                ),
                "test-system"
        );

        Instant createdAt = Instant.parse("2026-10-22T10:00:00Z");

        Order order = mapper.toDomain(request, "internal-789", createdAt);

        assertThat(order.orderId()).isEqualTo("internal-789");
        assertThat(order.externalOrderId()).isEqualTo("partner-123");
        assertThat(order.customerId()).isEqualTo("customer-456");
        assertThat(order.status()).isEqualTo(OrderStatus.IMPORTED);
        assertThat(order.totalAmount()).isEqualByComparingTo("35.00");
        assertThat(order.createdAt()).isEqualTo(createdAt);
        assertThat(order.items()).hasSize(2);
    }

    @Test
    void toResponseEchoesItems() {
        // TODO
        Order order = new Order(
                "internal-789",
                "partner-123",
                "customer-456",
                OrderStatus.IMPORTED,
                new BigDecimal("35.00"),
                Instant.parse("2026-10-22T10:00:00Z"),
                List.of(
                        new OrderItem("SKU-1", "Keyboard", 2, new BigDecimal("10.00")),
                        new OrderItem("SKU-2", "Mouse", 3, new BigDecimal("5.00"))
                )
        );

        OrderResponse response = mapper.toResponse(order);

        assertThat(response.orderId()).isEqualTo("internal-789");
        assertThat(response.externalOrderId()).isEqualTo("partner-123");
        assertThat(response.customerId()).isEqualTo("customer-456");
        assertThat(response.totalAmount()).isEqualByComparingTo("35.00");
        assertThat(response.items()).hasSize(2);
        assertThat(response.items())
                .extracting(OrderItemResponse::sku)
                .containsExactly("SKU-1", "SKU-2");
        assertThat(response.items())
                .extracting(OrderItemResponse::quantity)
                .containsExactly(2, 3);
    }
}
