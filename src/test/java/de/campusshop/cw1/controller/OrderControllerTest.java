package de.campusshop.cw1.controller;

import de.campusshop.cw1.service.OrderService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer tests for {@link OrderController} using {@link MockMvc} and a mocked service
 * assert that a valid {@code POST /partner-orders} returns
 * {@code 201} and an invalid one returns {@code 400}.
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService service;

    @Test
    void validPostReturns201() throws Exception {
        // stub service.importOrder(...), perform POST with a valid body, expect 201
        when(service.importOrder(any())).thenReturn(null);
        String json = """
                {
                    "partnerOrderId": "partner-123",
                    "buyer": {
                        "id": "buyer-123",
                        "fullName": "Test Buyer",
                        "email": "buyer@example.com"
                    },
                    "items": [
                        {
                            "sku": "SKU-123",
                            "name": "Test Product",
                            "quantity": 2,
                            "unitPrice": 10.00
                        }
                    ]
                }
                """;
        mockMvc.perform(post("/partner-orders")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void invalidPostReturns400() throws Exception {
        // perform POST with an invalid body (e.g. empty items), expect 400
        String json = """
            {
                "partnerOrderId": "partner-123",
                "buyer": {
                    "id": "customer-123"
                },
                "items": []
            }
            """;
        mockMvc.perform(post("/partner-orders")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
