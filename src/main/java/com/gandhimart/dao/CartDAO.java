package com.gandhimart.dao;

import com.gandhimart.model.CartItem;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CartDAO {

    void addItem(Long buyerId, Long productId, Integer quantity)
            throws SQLException;

    void updateQuantity(
            Long buyerId,
            Long productId,
            Integer quantity
    ) throws SQLException;

    void removeItem(
            Long buyerId,
            Long productId
    ) throws SQLException;

    Optional<CartItem> findItem(
            Long buyerId,
            Long productId
    ) throws SQLException;

    List<CartItem> findByBuyerId(Long buyerId)
            throws SQLException;

    void clearCart(Long buyerId) throws SQLException;
}