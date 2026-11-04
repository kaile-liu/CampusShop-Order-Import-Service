package de.campusshop.cw1.exception;

/** Thrown when an order reuses an existing externalOrderId. Mapped to HTTP 409. */
public class DuplicateOrderException extends RuntimeException {
    public DuplicateOrderException(String externalOrderId) {
        super("Order already imported for externalOrderId: " + externalOrderId);
    }
}
