package com.gandhimart.controller;

import com.gandhimart.dao.ProductDAOImpl;
import com.gandhimart.model.Product;
import com.gandhimart.service.ProductService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/api/v1/products/details")
public class ProductDetailsServlet extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() {
        productService = new ProductService(new ProductDAOImpl());
    }

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String productIdValue = request.getParameter("id");

        try {
            if (productIdValue == null ||
                    productIdValue.trim().isEmpty()) {

                response.setStatus(
                        HttpServletResponse.SC_BAD_REQUEST
                );

                response.getWriter().write(
                        "{\"error\":\"Product ID is required\"}"
                );
                return;
            }

            Long productId = Long.parseLong(productIdValue);

            Optional<Product> optionalProduct =
                    productService.getProduct(productId);

            if (optionalProduct.isEmpty()) {

                response.setStatus(
                        HttpServletResponse.SC_NOT_FOUND
                );

                response.getWriter().write(
                        "{\"error\":\"Product not found\"}"
                );
                return;
            }

            Product product = optionalProduct.get();

            response.setStatus(HttpServletResponse.SC_OK);

            response.getWriter().write(buildJson(product));

        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().write(
                    "{\"error\":\"Invalid product ID\"}"
            );

        } catch (IllegalArgumentException e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

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
                    "{\"error\":\"Unable to retrieve product\"}"
            );
        }
    }

    private String buildJson(Product product) {

        return "{"
                + "\"id\":" + product.getId() + ","
                + "\"sellerId\":" + product.getSellerId() + ","
                + "\"name\":\""
                + escapeJson(product.getName())
                + "\","
                + "\"description\":\""
                + escapeJson(product.getDescription())
                + "\","
                + "\"price\":" + product.getPrice() + ","
                + "\"stock\":" + product.getStock()
                + "}";
    }

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}