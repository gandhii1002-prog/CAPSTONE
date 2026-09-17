package com.gandhimart.service;

import com.gandhimart.dao.OrderDAO;
import com.gandhimart.dto.SellerOrderItem;
import com.gandhimart.model.Order;

import java.sql.SQLException;
import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO;

    public OrderService(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    public Long checkout(
            Long buyerId,
            boolean paymentConfirmed)
            throws SQLException {

        validateUserId(buyerId);

        if (!paymentConfirmed) {
            throw new IllegalArgumentException(
                    "Mock payment confirmation is required"
            );
        }

        return orderDAO.placeOrder(buyerId);
    }

    public List<Order> getBuyerOrders(
            Long buyerId) throws SQLException {

        validateUserId(buyerId);

        return orderDAO.findByBuyerId(buyerId);
    }

    public List<SellerOrderItem> getSellerOrders(
            Long sellerId) throws SQLException {

        validateUserId(sellerId);

        return orderDAO.findIncomingBySellerId(
                sellerId
        );
    }

    private void validateUserId(Long userId) {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid user"
            );
        }
    }
}