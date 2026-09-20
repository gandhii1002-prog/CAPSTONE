package com.gandhimart.dao;

import com.gandhimart.testutil.H2TestDatabase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderDAOImplTest {

    private DataSource dataSource;
    private OrderDAOImpl orderDAO;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = H2TestDatabase.create();
        orderDAO = new OrderDAOImpl(dataSource);
        insertUser(1L, "Order Buyer", "orderbuyer@test.com", "BUYER");
        insertUser(2L, "Order Seller", "orderseller@test.com", "SELLER");

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement("""
                     INSERT INTO products
                     (id, seller_id, name, description, price, stock_quantity)
                     VALUES (?, ?, ?, ?, ?, ?)
                     """)) {
            statement.setLong(1, 1L);
            statement.setLong(2, 2L);
            statement.setString(3, "Order Test Laptop");
            statement.setString(4, "Order integration test");
            statement.setBigDecimal(5, new BigDecimal("50000.00"));
            statement.setInt(6, 10);
            statement.executeUpdate();
        }

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement("""
                     INSERT INTO cart_items (buyer_id, product_id, quantity)
                     VALUES (?, ?, ?)
                     """)) {
            statement.setLong(1, 1L);
            statement.setLong(2, 1L);
            statement.setInt(3, 3);
            statement.executeUpdate();
        }
    }

    private void insertUser(Long id, String name, String email, String role)
            throws Exception {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement("""
                     INSERT INTO users
                     (id, name, email, password_hash, role)
                     VALUES (?, ?, ?, ?, ?)
                     """)) {
            statement.setLong(1, id);
            statement.setString(2, name);
            statement.setString(3, email);
            statement.setString(4, "hash");
            statement.setString(5, role);
            statement.executeUpdate();
        }
    }

    @Test
    void placeOrderShouldCreateOrder() throws Exception {
        Long orderId = orderDAO.placeOrder(1L);

        assertNotNull(orderId);
        assertTrue(orderId > 0);
    }

    @Test
    void placeOrderShouldDeductStockAndClearCart() throws Exception {
        Long orderId = orderDAO.placeOrder(1L);
        assertNotNull(orderId);

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(
                     "SELECT stock_quantity FROM products WHERE id = ?")) {
            statement.setLong(1, 1L);
            try (var resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals(7, resultSet.getInt("stock_quantity"));
            }
        }

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(
                     "SELECT COUNT(*) AS cart_count FROM cart_items WHERE buyer_id = ?")) {
            statement.setLong(1, 1L);
            try (var resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals(0, resultSet.getInt("cart_count"));
            }
        }
    }

    @Test
    void placeOrderShouldCreateOrderItem() throws Exception {
        Long orderId = orderDAO.placeOrder(1L);

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement("""
                     SELECT product_id, quantity, unit_price
                     FROM order_items WHERE order_id = ?
                     """)) {
            statement.setLong(1, orderId);
            try (var resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals(1L, resultSet.getLong("product_id"));
                assertEquals(3, resultSet.getInt("quantity"));
                assertEquals(new BigDecimal("50000.00"),
                        resultSet.getBigDecimal("unit_price"));
            }
        }
    }

    @Test
    void placeOrderShouldStoreCorrectTotal() throws Exception {
        Long orderId = orderDAO.placeOrder(1L);

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement("""
                     SELECT total_amount, status, payment_status
                     FROM orders WHERE id = ?
                     """)) {
            statement.setLong(1, orderId);
            try (var resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals(new BigDecimal("150000.00"),
                        resultSet.getBigDecimal("total_amount"));
                assertEquals("CONFIRMED", resultSet.getString("status"));
                assertEquals("PAID", resultSet.getString("payment_status"));
            }
        }
    }
}