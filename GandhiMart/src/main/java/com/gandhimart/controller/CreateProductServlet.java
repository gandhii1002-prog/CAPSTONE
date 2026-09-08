package com.gandhimart.controller;

import com.gandhimart.dao.ProductDAOImpl;
import com.gandhimart.service.ProductService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/api/v1/products")
public class CreateProductServlet extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() {
        productService = new ProductService(new ProductDAOImpl());
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        // User must be logged in.
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                    "{\"error\":\"Authentication required\"}"
            );
            return;
        }

        Object userIdObject = session.getAttribute("userId");
        Object roleObject = session.getAttribute("userRole");

        if (!(userIdObject instanceof Long)
                || roleObject == null) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                    "{\"error\":\"Invalid session\"}"
            );
            return;
        }

        Long sellerId = (Long) userIdObject;
        String role = roleObject.toString();

        // Only sellers can create products.
        if (!"SELLER".equals(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(
                    "{\"error\":\"Only sellers can create products\"}"
            );
            return;
        }

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceValue = request.getParameter("price");
        String stockValue = request.getParameter("stock");

        try {
            if (priceValue == null || stockValue == null) {
                throw new IllegalArgumentException(
                        "Price and stock are required"
                );
            }

            BigDecimal price = new BigDecimal(priceValue);
            Integer stock = Integer.valueOf(stockValue);

            productService.createProduct(
                    sellerId,
                    name,
                    description,
                    price,
                    stock
            );

            response.setStatus(HttpServletResponse.SC_CREATED);

            response.getWriter().write(
                    "{\"message\":\"Product created successfully\"}"
            );

        } catch (NumberFormatException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write(
                    "{\"error\":\"Invalid price or stock\"}"
            );

        } catch (IllegalArgumentException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write(
                    "{\"error\":\""
                            + escapeJson(e.getMessage())
                            + "\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().write(
                    "{\"error\":\"Product creation failed\"}"
            );
        }
    }

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}