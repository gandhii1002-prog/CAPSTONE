package com.gandhimart.service;

import com.gandhimart.dao.OrderDAO;

import java.sql.SQLException;

public class OrderService {

    private final OrderDAO orderDAO;

    public OrderService(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    public Long checkout(
            Long buyerId,
            boolean paymentConfirmed)
            throws SQLException {

        if (buyerId == null || buyerId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid buyer"
            );
        }

        if (!paymentConfirmed) {
            throw new IllegalArgumentException(
                    "Mock payment confirmation is required"
            );
        }

        return orderDAO.placeOrder(buyerId);
    }
}