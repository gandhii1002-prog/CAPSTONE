package com.gandhimart.dao;

import com.gandhimart.model.Product;
import com.gandhimart.model.Review;
import com.gandhimart.testutil.H2TestDatabase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewDAOImplTest {

    private DataSource dataSource;
    private ReviewDAOImpl reviewDAO;

    @BeforeEach
    void setUp() throws Exception {

        dataSource = H2TestDatabase.create();

        reviewDAO =
                new ReviewDAOImpl(dataSource);

        insertBuyer();
        insertSeller();
        insertProduct();
        insertCompletedOrder();
    }

    private void insertBuyer() throws Exception {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(
                     """
                     INSERT INTO users
                     (id, name, email, password_hash, role)
                     VALUES (?, ?, ?, ?, ?)
                     """)) {

            statement.setLong(1, 1L);
            statement.setString(2, "Test Buyer");
            statement.setString(3, "buyer@test.com");
            statement.setString(4, "hash");
            statement.setString(5, "BUYER");
            statement.executeUpdate();
        }
    }

    private void insertSeller() throws Exception {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(
                     """
                     INSERT INTO users
                     (id, name, email, password_hash, role)
                     VALUES (?, ?, ?, ?, ?)
                     """)) {

            statement.setLong(1, 2L);
            statement.setString(2, "Test Seller");
            statement.setString(3, "seller@test.com");
            statement.setString(4, "hash");
            statement.setString(5, "SELLER");
            statement.executeUpdate();
        }
    }

    private void insertProduct() throws Exception {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(
                     """
                     INSERT INTO products
                     (id, seller_id, name, description,
                      price, stock_quantity)
                     VALUES (?, ?, ?, ?, ?, ?)
                     """)) {

            statement.setLong(1, 1L);
            statement.setLong(2, 2L);
            statement.setString(3, "Review Test Product");
            statement.setString(4, "Review testing");
            statement.setBigDecimal(
                    5,
                    new BigDecimal("1000.00")
            );
            statement.setInt(6, 10);

            statement.executeUpdate();
        }
    }

    private void insertCompletedOrder()
            throws Exception {

        try (var connection = dataSource.getConnection();
             var orderStatement =
                     connection.prepareStatement(
                             """
                             INSERT INTO orders
                             (id, buyer_id, total_amount,
                              status, payment_status)
                             VALUES (?, ?, ?, ?, ?)
                             """
                     );
             var itemStatement =
                     connection.prepareStatement(
                             """
                             INSERT INTO order_items
                             (order_id, product_id,
                              quantity, unit_price)
                             VALUES (?, ?, ?, ?)
                             """
                     )) {

            orderStatement.setLong(1, 1L);
            orderStatement.setLong(2, 1L);
            orderStatement.setBigDecimal(
                    3,
                    new BigDecimal("1000.00")
            );
            orderStatement.setString(
                    4,
                    "CONFIRMED"
            );
            orderStatement.setString(
                    5,
                    "PAID"
            );
            orderStatement.executeUpdate();

            itemStatement.setLong(1, 1L);
            itemStatement.setLong(2, 1L);
            itemStatement.setInt(3, 1);
            itemStatement.setBigDecimal(
                    4,
                    new BigDecimal("1000.00")
            );
            itemStatement.executeUpdate();
        }
    }

    @Test
    void shouldDetectCompletedPurchase()
            throws Exception {

        assertTrue(
                reviewDAO.hasCompletedPurchase(
                        1L,
                        1L
                )
        );
    }

    @Test
    void shouldCreateAndReadReview()
            throws Exception {

        Review review =
                new Review();

        review.setProductId(1L);
        review.setUserId(1L);
        review.setRating(5);
        review.setComment(
                "Excellent product"
        );

        Long reviewId =
                reviewDAO.create(review);

        assertNotNull(reviewId);

        List<?> reviews =
                reviewDAO.findByProductId(1L);

        assertEquals(
                1,
                reviews.size()
        );
    }

    @Test
    void shouldReturnReviewableProduct()
            throws Exception {

        List<Product> products =
                reviewDAO.findReviewableProducts(1L);

        assertEquals(
                1,
                products.size()
        );

        assertEquals(
                "Review Test Product",
                products.get(0).getName()
        );
    }
}