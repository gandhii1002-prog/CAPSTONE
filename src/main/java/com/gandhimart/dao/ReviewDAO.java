package com.gandhimart.dao;

import com.gandhimart.dto.ReviewResponse;
import com.gandhimart.model.Product;
import com.gandhimart.model.Review;

import java.sql.SQLException;
import java.util.List;

public interface ReviewDAO {

    Long create(Review review) throws SQLException;

    boolean hasCompletedPurchase(Long userId, Long productId)
            throws SQLException;

    boolean existsByUserAndProduct(Long userId, Long productId)
            throws SQLException;

    List<ReviewResponse> findByProductId(Long productId)
            throws SQLException;

    List<Product> findReviewableProducts(Long userId)
            throws SQLException;
}
