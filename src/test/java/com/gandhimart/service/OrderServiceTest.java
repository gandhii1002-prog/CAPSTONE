package com.gandhimart.service;

import com.gandhimart.dao.OrderDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderDAO orderDAO;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderDAO = mock(OrderDAO.class);
        orderService = new OrderService(orderDAO);
    }

    @Test
    void checkoutShouldRequirePaymentConfirmation() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> orderService.checkout(
                                1L,
                                false
                        )
                );

        assertEquals(
                "Mock payment confirmation is required",
                exception.getMessage()
        );

        verifyNoInteractions(orderDAO);
    }

    @Test
    void checkoutShouldPlaceOrderWhenPaymentConfirmed()
            throws Exception {

        when(
                orderDAO.placeOrder(1L)
        ).thenReturn(10L);

        Long orderId =
                orderService.checkout(
                        1L,
                        true
                );

        assertEquals(
                10L,
                orderId
        );

        verify(
                orderDAO
        ).placeOrder(1L);
    }
}