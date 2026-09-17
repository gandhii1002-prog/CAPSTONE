package com.gandhimart.dao;

import com.gandhimart.model.Order;

import java.sql.SQLException;
import java.util.Optional;

public interface OrderDAO {

    Long placeOrder(Long buyerId) throws SQLException;

    Optional<Order> findById(Long orderId) throws SQLException;
}