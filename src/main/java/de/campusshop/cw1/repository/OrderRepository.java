package de.campusshop.cw1.repository;

import de.campusshop.cw1.model.Order;

import java.util.List;
import java.util.Optional;

/**
 * Persistence abstraction for orders. Keeping this an interface isolates the
 * file-based implementation from the service (Week 3 / Week 5).
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(String orderId);

    List<Order> findAll();

    List<Order> findByCustomerId(String customerId);

    /** @return {@code true} if an order was removed, {@code false} if none existed. */
    boolean deleteById(String orderId);

    /**
     * Whether an order with this {@code externalOrderId} (the partner's {@code partnerOrderId})
     * already exists. Orders are always unique, so the service calls this before {@link #save} and
     * rejects duplicates with {@code 409} (CW1 spec §5.2).
     */
    boolean existsByExternalOrderId(String externalOrderId);
}
