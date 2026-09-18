package com.gandhimart.service;

import com.gandhimart.dao.ReviewDAO;
import com.gandhimart.dto.ReviewResponse;
import com.gandhimart.model.Product;
import com.gandhimart.model.Review;

import java.sql.SQLException;
import java.util.List;

public class ReviewService {

    private final ReviewDAO reviewDAO;

    public ReviewService(ReviewDAO reviewDAO) {
        this.reviewDAO = reviewDAO;
    }

    public Long createReview(Long userId, Long productId, Integer rating,
                             String comment) throws SQLException {
        validateUserId(userId);
        validateProductId(productId);
        validateRating(rating);

        String cleanedComment = comment == null ? null : comment.trim();
        if (cleanedComment != null && cleanedComment.length() > 1000) {
            throw new IllegalArgumentException(
                    "Comment must not exceed 1000 characters");
        }

        if (!reviewDAO.hasCompletedPurchase(userId, productId)) {
            throw new IllegalArgumentException(
                    "You can review only products from completed paid orders");
        }

        if (reviewDAO.existsByUserAndProduct(userId, productId)) {
            throw new IllegalArgumentException(
                    "You have already reviewed this product");
        }

        Review review = new Review();
        review.setProductId(productId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setComment(cleanedComment);
        return reviewDAO.create(review);
    }

    public List<ReviewResponse> getProductReviews(Long productId)
            throws SQLException {
        validateProductId(productId);
        return reviewDAO.findByProductId(productId);
    }

    public List<Product> getReviewableProducts(Long userId)
            throws SQLException {
        validateUserId(userId);
        return reviewDAO.findReviewableProducts(userId);
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user");
        }
    }

    private void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Invalid product");
        }
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }
}
