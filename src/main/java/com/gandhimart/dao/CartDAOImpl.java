package com.gandhimart.dao;

import com.gandhimart.model.CartItem;
import com.gandhimart.util.DatabaseConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartDAOImpl implements CartDAO {

    private final DataSource dataSource;

    public CartDAOImpl() {
        this(DatabaseConfig.getDataSource());
    }

    public CartDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void addItem(
            Long buyerId,
            Long productId,
            Integer quantity) throws SQLException {

        String findSql = """
                SELECT quantity
                FROM cart_items
                WHERE buyer_id = ?
                  AND product_id = ?
                """;

        String insertSql = """
                INSERT INTO cart_items
                (buyer_id, product_id, quantity)
                VALUES (?, ?, ?)
                """;

        String updateSql = """
                UPDATE cart_items
                SET quantity = ?
                WHERE buyer_id = ?
                  AND product_id = ?
                """;

        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(false);

            try {
                Integer existingQuantity = null;

                try (PreparedStatement statement =
                             connection.prepareStatement(findSql)) {

                    statement.setLong(1, buyerId);
                    statement.setLong(2, productId);

                    try (ResultSet resultSet =
                                 statement.executeQuery()) {

                        if (resultSet.next()) {
                            existingQuantity =
                                    resultSet.getInt("quantity");
                        }
                    }
                }

                if (existingQuantity == null) {

                    try (PreparedStatement statement =
                                 connection.prepareStatement(insertSql)) {

                        statement.setLong(1, buyerId);
                        statement.setLong(2, productId);
                        statement.setInt(3, quantity);

                        statement.executeUpdate();
                    }

                } else {

                    try (PreparedStatement statement =
                                 connection.prepareStatement(updateSql)) {

                        statement.setInt(
                                1,
                                existingQuantity + quantity
                        );
                        statement.setLong(2, buyerId);
                        statement.setLong(3, productId);

                        statement.executeUpdate();
                    }
                }

                connection.commit();

            } catch (SQLException e) {

                connection.rollback();
                throw e;

            } finally {

                connection.setAutoCommit(true);
            }
        }
    }

    @Override
    public void updateQuantity(
            Long buyerId,
            Long productId,
            Integer quantity) throws SQLException {

        String sql = """
                UPDATE cart_items
                SET quantity = ?
                WHERE buyer_id = ?
                  AND product_id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setLong(2, buyerId);
            statement.setLong(3, productId);

            statement.executeUpdate();
        }
    }

    @Override
    public void removeItem(
            Long buyerId,
            Long productId) throws SQLException {

        String sql = """
                DELETE FROM cart_items
                WHERE buyer_id = ?
                  AND product_id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, buyerId);
            statement.setLong(2, productId);

            statement.executeUpdate();
        }
    }

    @Override
    public Optional<CartItem> findItem(
            Long buyerId,
            Long productId) throws SQLException {

        String sql = """
                SELECT
                    ci.id,
                    ci.buyer_id,
                    ci.product_id,
                    p.name AS product_name,
                    p.price AS product_price,
                    ci.quantity
                FROM cart_items ci
                JOIN products p
                    ON p.id = ci.product_id
                WHERE ci.buyer_id = ?
                  AND ci.product_id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, buyerId);
            statement.setLong(2, productId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapCartItem(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<CartItem> findByBuyerId(
            Long buyerId) throws SQLException {

        String sql = """
                SELECT
                    ci.id,
                    ci.buyer_id,
                    ci.product_id,
                    p.name AS product_name,
                    p.price AS product_price,
                    ci.quantity
                FROM cart_items ci
                JOIN products p
                    ON p.id = ci.product_id
                WHERE ci.buyer_id = ?
                ORDER BY ci.id
                """;

        List<CartItem> cartItems = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, buyerId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    cartItems.add(mapCartItem(resultSet));
                }
            }
        }

        return cartItems;
    }

    @Override
    public void clearCart(Long buyerId) throws SQLException {

        String sql = """
                DELETE FROM cart_items
                WHERE buyer_id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, buyerId);

            statement.executeUpdate();
        }
    }

    private CartItem mapCartItem(
            ResultSet resultSet) throws SQLException {

        CartItem cartItem = new CartItem();

        cartItem.setId(
                resultSet.getLong("id")
        );

        cartItem.setBuyerId(
                resultSet.getLong("buyer_id")
        );

        cartItem.setProductId(
                resultSet.getLong("product_id")
        );

        cartItem.setProductName(
                resultSet.getString("product_name")
        );

        cartItem.setProductPrice(
                resultSet.getBigDecimal("product_price")
        );

        cartItem.setQuantity(
                resultSet.getInt("quantity")
        );

        if (cartItem.getProductPrice() != null) {
            cartItem.setSubtotal(
                    cartItem.getProductPrice()
                            .multiply(
                                    java.math.BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            )
            );
        }

        return cartItem;
    }
}