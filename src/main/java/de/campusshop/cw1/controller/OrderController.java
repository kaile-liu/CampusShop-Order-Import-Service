package de.campusshop.cw1.controller;

import de.campusshop.cw1.dto.OrderResponse;
import de.campusshop.cw1.dto.PartnerOrderRequest;
import de.campusshop.cw1.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API for the CampusShop order import service (Week 4).
 *
 * <p>The endpoints are already wired to the service layer — your work happens in
 * {@link OrderService} (and below). Validation is triggered by {@code @Valid};
 * not-found / duplicate are handled by {@link GlobalExceptionHandler}.
 */
@RestController
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/partner-orders")
    public ResponseEntity<OrderResponse> importOrder(@Valid @RequestBody PartnerOrderRequest request) {
        OrderResponse response = service.importOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/orders")
    public List<OrderResponse> getAllOrders() {
        return service.findAll();
    }

    @GetMapping("/orders/{id}")
    public OrderResponse getOrder(@PathVariable String id) {
        return service.findById(id);
    }

    @GetMapping("/orders/customer/{customerId}")
    public List<OrderResponse> getOrdersByCustomer(@PathVariable String customerId) {
        return service.findByCustomerId(customerId);
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
