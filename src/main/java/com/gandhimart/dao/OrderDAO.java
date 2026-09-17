package com.gandhimart.dao;

import com.gandhimart.dto.SellerOrderItem;
import com.gandhimart.model.Order;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface OrderDAO {

    Long placeOrder(Long buyerId) throws SQLException;

    Optional<Order> findById(Long orderId) throws SQLException;

    List<Order> findByBuyerId(Long buyerId)
            throws SQLException;

    List<SellerOrderItem> findIncomingBySellerId(Long sellerId)
            throws SQLException;
}