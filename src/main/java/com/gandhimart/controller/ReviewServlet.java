package com.gandhimart.controller;

import com.gandhimart.dao.ReviewDAOImpl;
import com.gandhimart.dto.ReviewResponse;
import com.gandhimart.model.Product;
import com.gandhimart.service.ReviewService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/v1/reviews/*")
public class ReviewServlet extends HttpServlet {

    private ReviewService reviewService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        reviewService = new ReviewService(new ReviewDAOImpl());
        gson = new GsonBuilder()
            .registerTypeAdapter(
                LocalDateTime.class,
                (JsonSerializer<LocalDateTime>) (src, type, context) ->
                    new JsonPrimitive(src.toString()))
            .create();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        try {
            if ("/reviewable".equals(request.getPathInfo())) {
                getReviewableProducts(request, response);
                return;
            }

            Long productId = parseLong(request.getParameter("productId"));
            List<ReviewResponse> reviews =
                    reviewService.getProductReviews(productId);

            Map<String, Object> result = new HashMap<>();
            result.put("productId", productId);
            result.put("reviews", reviews);
            result.put("count", reviews.size());
            result.put("averageRating", reviews.stream()
                    .mapToInt(ReviewResponse::getRating)
                    .average()
                    .orElse(0.0));

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(result));
        } catch (IllegalArgumentException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load reviews");
        }
    }

    private void getReviewableProducts(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Authentication required");
            return;
        }

        String role = String.valueOf(session.getAttribute("userRole"));
        if (!"BUYER".equalsIgnoreCase(role)) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Only buyers can access reviewable products");
            return;
        }

        try {
            Long userId = ((Number) session.getAttribute("userId")).longValue();
            List<Product> products =
                    reviewService.getReviewableProducts(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("products", products);
            result.put("count", products.size());

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(result));
        } catch (Exception e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load reviewable products");
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Authentication required");
            return;
        }

        String role = String.valueOf(session.getAttribute("userRole"));
        if (!"BUYER".equalsIgnoreCase(role)) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Only buyers can submit reviews");
            return;
        }

        try {
            Long userId = ((Number) session.getAttribute("userId")).longValue();
            Long productId = parseLong(request.getParameter("productId"));
            Integer rating = parseInteger(request.getParameter("rating"));
            Long reviewId = reviewService.createReview(
                    userId,
                    productId,
                    rating,
                    request.getParameter("comment"));

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(Map.of(
                    "message", "Review submitted successfully",
                    "reviewId", reviewId)));
        } catch (IllegalArgumentException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to submit review");
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID is required");
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid product ID");
        }
    }

    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Rating is required");
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid rating");
        }
    }

    private void writeError(
            HttpServletResponse response,
            int status,
            String message) throws IOException {
        response.setStatus(status);
        response.getWriter().write(gson.toJson(Map.of("error", message)));
    }
}
