package com.gandhimart.controller;

import com.gandhimart.dao.ProductDAOImpl;
import com.gandhimart.model.Product;
import com.gandhimart.service.ProductService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/api/v1/products")
public class ProductListServlet extends HttpServlet {

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

        String keyword = request.getParameter("keyword");
        String minPriceValue = request.getParameter("minPrice");
        String maxPriceValue = request.getParameter("maxPrice");

        try {
            BigDecimal minPrice = parsePrice(minPriceValue);
            BigDecimal maxPrice = parsePrice(maxPriceValue);

            List<Product> products;

            if ((keyword == null || keyword.trim().isEmpty())
                    && minPrice == null
                    && maxPrice == null) {

                products = productService.getAllProducts();

            } else {

                products = productService.searchProducts(
                        keyword,
                        minPrice,
                        maxPrice
                );
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(
                    buildJson(products)
            );

        } catch (NumberFormatException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                    "{\"error\":\"Invalid price filter\"}"
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
                    "{\"error\":\"Unable to retrieve products\"}"
            );
        }
    }

    private BigDecimal parsePrice(String value) {

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return new BigDecimal(value.trim());
    }

    private String buildJson(List<Product> products) {

        StringBuilder json = new StringBuilder();
        json.append("{\"products\":[");

        for (int i = 0; i < products.size(); i++) {

            Product product = products.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{")
                    .append("\"id\":").append(product.getId()).append(",")
                    .append("\"sellerId\":").append(product.getSellerId()).append(",")
                    .append("\"name\":\"")
                    .append(escapeJson(product.getName()))
                    .append("\",")
                    .append("\"description\":\"")
                    .append(escapeJson(product.getDescription()))
                    .append("\",")
                    .append("\"price\":")
                    .append(product.getPrice())
                    .append(",")
                    .append("\"stock\":")
                    .append(product.getStock())
                    .append("}");
        }

        json.append("]}");

        return json.toString();
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