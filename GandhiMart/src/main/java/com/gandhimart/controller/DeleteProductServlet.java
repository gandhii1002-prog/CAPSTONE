package com.gandhimart.controller;

import com.gandhimart.dao.ProductDAOImpl;
import com.gandhimart.service.ProductService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/v1/products/delete")
public class DeleteProductServlet extends HttpServlet {

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

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                    "{\"error\":\"Authentication required\"}"
            );
            return;
        }

        Object userIdObject = session.getAttribute("userId");
        Object roleObject = session.getAttribute("userRole");

        if (!(userIdObject instanceof Long) || roleObject == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                    "{\"error\":\"Invalid session\"}"
            );
            return;
        }

        Long sellerId = (Long) userIdObject;
        String role = roleObject.toString();

        if (!"SELLER".equals(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(
                    "{\"error\":\"Only sellers can delete products\"}"
            );
            return;
        }

        String productIdValue = request.getParameter("productId");

        try {
            if (productIdValue == null || productIdValue.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Product ID is required"
                );
            }

            Long productId = Long.valueOf(productIdValue);

            productService.deleteProduct(sellerId, productId);

            response.setStatus(HttpServletResponse.SC_OK);

            response.getWriter().write(
                    "{\"message\":\"Product deleted successfully\"}"
            );

        } catch (NumberFormatException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write(
                    "{\"error\":\"Invalid product ID\"}"
            );

        } catch (IllegalArgumentException e) {

            String message = e.getMessage();

            if ("Product not found".equals(message)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else if (message != null &&
                    message.contains("not allowed")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

            response.getWriter().write(
                    "{\"error\":\""
                            + escapeJson(message)
                            + "\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().write(
                    "{\"error\":\"Product deletion failed\"}"
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