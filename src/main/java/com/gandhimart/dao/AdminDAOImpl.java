package com.gandhimart.dao;

import com.gandhimart.dto.AdminOrderView;
import com.gandhimart.model.Product;
import com.gandhimart.model.User;
import com.gandhimart.util.DatabaseConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AdminDAOImpl implements AdminDAO {

    private final DataSource dataSource;

    public AdminDAOImpl() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    @Override
    public List<User> findAllUsers()
            throws SQLException {

        String sql = """
                SELECT
                    id,
                    name,
                    email,
                    role,
                    created_at
                FROM users
                ORDER BY created_at DESC
                """;

        List<User> users = new ArrayList<>();

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                User user = new User();

                user.setId(
                        resultSet.getLong("id")
                );

                user.setName(
                        resultSet.getString("name")
                );

                user.setEmail(
                        resultSet.getString("email")
                );

                user.setRole(
                        resultSet.getString("role")
                );

                Timestamp timestamp =
                        resultSet.getTimestamp("created_at");

                if (timestamp != null) {
                    user.setCreatedAt(
                            timestamp.toLocalDateTime()
                    );
                }

                users.add(user);
            }
        }

        return users;
    }

    @Override
    public List<AdminOrderView> findAllOrders()
            throws SQLException {

        String sql = """
                SELECT
                    o.id AS order_id,
                    o.buyer_id,
                    u.name AS buyer_name,
                    o.status,
                    o.payment_status,
                    o.total_amount,
                    o.created_at
                FROM orders o
                JOIN users u
                    ON u.id = o.buyer_id
                ORDER BY o.created_at DESC,
                         o.id DESC
                """;

        List<AdminOrderView> orders =
                new ArrayList<>();

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                AdminOrderView order =
                        new AdminOrderView();

                order.setOrderId(
                        resultSet.getLong("order_id")
                );

                order.setBuyerId(
                        resultSet.getLong("buyer_id")
                );

                order.setBuyerName(
                        resultSet.getString("buyer_name")
                );

                order.setStatus(
                        resultSet.getString("status")
                );

                order.setPaymentStatus(
                        resultSet.getString(
                                "payment_status"
                        )
                );

                order.setTotalAmount(
                        resultSet.getBigDecimal(
                                "total_amount"
                        )
                );

                Timestamp timestamp =
                        resultSet.getTimestamp(
                                "created_at"
                        );

                if (timestamp != null) {
                    order.setCreatedAt(
                            timestamp.toLocalDateTime()
                    );
                }

                orders.add(order);
            }
        }

        return orders;
    }

    @Override
    public List<Product> findAllProducts()
            throws SQLException {

        String sql = """
                SELECT
                    id,
                    seller_id,
                    name,
                    description,
                    price,
                    stock_quantity,
                    active,
                    created_at
                FROM products
                ORDER BY created_at DESC
                """;

        List<Product> products =
                new ArrayList<>();

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                Product product =
                        new Product();

                product.setId(
                        resultSet.getLong("id")
                );

                product.setSellerId(
                        resultSet.getLong("seller_id")
                );

                product.setName(
                        resultSet.getString("name")
                );

                product.setDescription(
                        resultSet.getString(
                                "description"
                        )
                );

                product.setPrice(
                        resultSet.getBigDecimal("price")
                );

                product.setStock(
                        resultSet.getInt(
                                "stock_quantity"
                        )
                );

                product.setActive(
                        resultSet.getBoolean("active")
                );

                Timestamp timestamp =
                        resultSet.getTimestamp("created_at");

                if (timestamp != null) {
                    product.setCreatedAt(
                            timestamp.toLocalDateTime()
                    );
                }

                products.add(product);
            }
        }

        return products;
    }

    @Override
    public void deleteProduct(Long productId)
            throws SQLException {

        String sql = """
                UPDATE products
                SET active = FALSE
                WHERE id = ?
                """;

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, productId);

            int updated =
                    statement.executeUpdate();

            if (updated != 1) {
                throw new SQLException(
                        "Product not found"
                );
            }
        }
    }
}