package de.campusshop.cw1.service;

import de.campusshop.cw1.dto.OrderResponse;
import de.campusshop.cw1.dto.PartnerOrderRequest;
import de.campusshop.cw1.exception.DuplicateOrderException;
import de.campusshop.cw1.exception.OrderNotFoundException;
import de.campusshop.cw1.mapper.OrderMapper;
import de.campusshop.cw1.repository.OrderRepository;
import de.campusshop.cw1.model.Order;
import de.campusshop.cw1.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link OrderService} with a <b>mocked</b> {@link OrderRepository}.
 * use Mockito to stub the repository and verify the service behaviour.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    private OrderService service;

    private final Clock clock = Clock.fixed(Instant.parse("2026-05-01T12:00:00Z"), ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        service = new OrderService(repository, new OrderMapper(), clock);
    }

    @Test
    void importOrderPersistsAndReturnsResponse() {
        // stub repository.findAll()/save(...), call service.importOrder(...), assert the response
        PartnerOrderRequest request = new PartnerOrderRequest(
                "partner-123",
                new de.campusshop.cw1.dto.PartnerBuyer(
                        "customer-123",
                        "Test Customer",
                        "customer@example.com"
                ),
                List.of(new de.campusshop.cw1.dto.PartnerItem(
                        "SKU-123",
                        "Test Product",
                        2,
                        new BigDecimal("10.00")
                )),
                "test-system"
        );

        when(repository.existsByExternalOrderId("partner-123"))
                .thenReturn(false);

        when(repository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = service.importOrder(request);

        assertNotNull(response);
        verify(repository).existsByExternalOrderId("partner-123");

        verify(repository).save(argThat(order ->
                order.externalOrderId().equals("partner-123")
                        && order.customerId().equals("customer-123")
                        && order.status() == OrderStatus.IMPORTED
                        && order.createdAt().equals(clock.instant())
                        && order.orderId() != null
                        && !order.orderId().isBlank()
        ));
    }

    @Test
    void importOrderRejectsDuplicatePartnerOrderId() {
        // arrange an existing order with the same partnerOrderId, expect a DuplicateOrderException
        PartnerOrderRequest request = new PartnerOrderRequest(
                "partner-123",
                new de.campusshop.cw1.dto.PartnerBuyer(
                        "customer-123",
                        "Test Customer",
                        "customer@example.com"
                ),
                List.of(new de.campusshop.cw1.dto.PartnerItem(
                        "SKU-123",
                        "Test Product",
                        2,
                        new BigDecimal("10.00")
                )),
                "test-system"
        );

        when(repository.existsByExternalOrderId("partner-123"))
                .thenReturn(true);

        assertThrows(
                DuplicateOrderException.class,
                () -> service.importOrder(request)
        );

        verify(repository, never()).save(any(Order.class));
    }

    @Test
    void findByIdThrowsWhenMissing() {
        // expect OrderNotFoundException
        when(repository.findById("missing-id"))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> service.findById("missing-id")
        );

        verify(repository).findById("missing-id");
    }
}
