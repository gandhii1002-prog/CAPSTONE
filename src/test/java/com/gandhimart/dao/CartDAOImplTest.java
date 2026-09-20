package com.gandhimart.dao;

import com.gandhimart.model.CartItem;
import com.gandhimart.testutil.H2TestDatabase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CartDAOImplTest {

    private DataSource dataSource;
    private CartDAOImpl cartDAO;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = H2TestDatabase.create();
        cartDAO = new CartDAOImpl(dataSource);
        insertUser(1L, "Cart Buyer", "cartbuyer@test.com", "BUYER");
        insertUser(2L, "Cart Seller", "cartseller@test.com", "SELLER");

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement("""
                     INSERT INTO products
                     (id, seller_id, name, description, price, stock_quantity)
                     VALUES (?, ?, ?, ?, ?, ?)
                     """)) {
            statement.setLong(1, 1L);
            statement.setLong(2, 2L);
            statement.setString(3, "Cart Test Laptop");
            statement.setString(4, "Cart integration test");
            statement.setBigDecimal(5, new BigDecimal("50000.00"));
            statement.setInt(6, 10);
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
    void shouldAddAndFindCartItem() throws Exception {
        cartDAO.addItem(1L, 1L, 2);

        Optional<CartItem> result = cartDAO.findItem(1L, 1L);

        assertTrue(result.isPresent());
        CartItem item = result.get();
        assertEquals(1L, item.getBuyerId());
        assertEquals(1L, item.getProductId());
        assertEquals(2, item.getQuantity());
        assertEquals("Cart Test Laptop", item.getProductName());
        assertEquals(new BigDecimal("100000.00"), item.getSubtotal());
    }

    @Test
    void shouldUpdateCartQuantity() throws Exception {
        cartDAO.addItem(1L, 1L, 2);
        cartDAO.updateQuantity(1L, 1L, 5);

        Optional<CartItem> result = cartDAO.findItem(1L, 1L);

        assertTrue(result.isPresent());
        assertEquals(5, result.get().getQuantity());
        assertEquals(new BigDecimal("250000.00"), result.get().getSubtotal());
    }

    @Test
    void shouldRemoveCartItem() throws Exception {
        cartDAO.addItem(1L, 1L, 2);
        cartDAO.removeItem(1L, 1L);

        assertTrue(cartDAO.findItem(1L, 1L).isEmpty());
    }

    @Test
    void shouldClearBuyerCart() throws Exception {
        cartDAO.addItem(1L, 1L, 2);

        assertEquals(1, cartDAO.findByBuyerId(1L).size());

        cartDAO.clearCart(1L);

        List<CartItem> after = cartDAO.findByBuyerId(1L);
        assertTrue(after.isEmpty());
    }
}