package com.gandhimart.dao;

import com.gandhimart.dto.ReviewResponse;
import com.gandhimart.model.Product;
import com.gandhimart.model.Review;
import com.gandhimart.util.DatabaseConfig;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAOImpl implements ReviewDAO {

    private final DataSource dataSource;

    public ReviewDAOImpl() {
        this(DatabaseConfig.getDataSource());
    }

    public ReviewDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Long create(Review review) throws SQLException {
        String sql = """
                INSERT INTO reviews
                (product_id, buyer_id, rating, comment)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, review.getProductId());
            statement.setLong(2, review.getUserId());
            statement.setInt(3, review.getRating());

            if (review.getComment() == null) {
                statement.setNull(4, java.sql.Types.VARCHAR);
            } else {
                statement.setString(4, review.getComment());
            }

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Unable to create review");
                }
                return keys.getLong(1);
            }
        }
    }

    @Override
    public boolean hasCompletedPurchase(Long userId, Long productId)
            throws SQLException {
        String sql = """
                SELECT 1
                FROM orders o
                JOIN order_items oi ON oi.order_id = o.id
                WHERE o.buyer_id = ?
                  AND oi.product_id = ?
                  AND o.payment_status = 'PAID'
                  AND o.status IN ('CONFIRMED', 'SHIPPED', 'DELIVERED')
                LIMIT 1
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);
            statement.setLong(2, productId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public boolean existsByUserAndProduct(Long userId, Long productId)
            throws SQLException {
        String sql = """
                SELECT 1
                FROM reviews
                WHERE buyer_id = ?
                  AND product_id = ?
                LIMIT 1
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);
            statement.setLong(2, productId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public List<ReviewResponse> findByProductId(Long productId)
            throws SQLException {
        String sql = """
                SELECT r.id AS review_id,
                       r.product_id,
                       r.rating,
                       r.comment,
                       r.created_at,
                       u.name AS reviewer_name
                FROM reviews r
                JOIN users u ON u.id = r.buyer_id
                WHERE r.product_id = ?
                ORDER BY r.created_at DESC, r.id DESC
                """;

        List<ReviewResponse> reviews = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, productId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ReviewResponse review = new ReviewResponse();
                    review.setReviewId(resultSet.getLong("review_id"));
                    review.setProductId(resultSet.getLong("product_id"));
                    review.setReviewerName(resultSet.getString("reviewer_name"));
                    review.setRating(resultSet.getInt("rating"));
                    review.setComment(resultSet.getString("comment"));

                    Timestamp timestamp = resultSet.getTimestamp("created_at");
                    if (timestamp != null) {
                        review.setCreatedAt(timestamp.toLocalDateTime());
                    }

                    reviews.add(review);
                }
            }
        }

        return reviews;
    }

    @Override
    public List<Product> findReviewableProducts(Long userId)
            throws SQLException {
        String sql = """
                SELECT DISTINCT
                       p.id,
                       p.seller_id,
                       p.name,
                       p.description,
                       p.price,
                       p.stock_quantity,
                       p.active,
                       p.created_at
                FROM orders o
                JOIN order_items oi ON oi.order_id = o.id
                JOIN products p ON p.id = oi.product_id
                LEFT JOIN reviews r
                    ON r.product_id = p.id
                   AND r.buyer_id = o.buyer_id
                WHERE o.buyer_id = ?
                  AND o.payment_status = 'PAID'
                  AND o.status IN ('CONFIRMED', 'SHIPPED', 'DELIVERED')
                  AND r.id IS NULL
                ORDER BY p.created_at DESC
                """;

        List<Product> products = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = new Product();
                    product.setId(resultSet.getLong("id"));
                    product.setSellerId(resultSet.getLong("seller_id"));
                    product.setName(resultSet.getString("name"));
                    product.setDescription(resultSet.getString("description"));
                    product.setPrice(resultSet.getBigDecimal("price"));
                    product.setStock(resultSet.getInt("stock_quantity"));
                    product.setActive(resultSet.getBoolean("active"));

                    Timestamp timestamp = resultSet.getTimestamp("created_at");
                    if (timestamp != null) {
                        product.setCreatedAt(timestamp.toLocalDateTime());
                    }

                    products.add(product);
                }
            }
        }

        return products;
    }
}
