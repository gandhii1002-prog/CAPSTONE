package com.gandhimart.controller;

import com.gandhimart.dao.AdminDAOImpl;
import com.gandhimart.dto.AdminOrderView;
import com.gandhimart.model.Product;
import com.gandhimart.model.User;
import com.gandhimart.service.AdminService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/v1/admin/*")
public class AdminServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() {

        adminService =
                new AdminService(
                        new AdminDAOImpl()
                );
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        if (!authorizeAdmin(request, response)) {
            return;
        }

        response.setContentType(
                "application/json"
        );

        response.setCharacterEncoding(
                "UTF-8"
        );

        String path = request.getPathInfo();

        if (path == null) {
            path = "";
        }

        try {

            switch (path) {

                case "/users":
                    response.getWriter().write(
                            buildUsersJson(
                                    adminService.getAllUsers()
                            )
                    );
                    break;

                case "/orders":
                    response.getWriter().write(
                            buildOrdersJson(
                                    adminService.getAllOrders()
                            )
                    );
                    break;

                case "/products":
                    response.getWriter().write(
                            buildProductsJson(
                                    adminService.getAllProducts()
                            )
                    );
                    break;

                default:
                    response.setStatus(
                            HttpServletResponse.SC_NOT_FOUND
                    );

                    response.getWriter().write(
                            "{\"error\":\"Admin endpoint not found\"}"
                    );
                    return;
            }

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().write(
                    "{\"error\":\"Unable to load admin data\"}"
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        if (!authorizeAdmin(request, response)) {
            return;
        }

        response.setContentType(
                "application/json"
        );

        response.setCharacterEncoding(
                "UTF-8"
        );

        String path = request.getPathInfo();

        if (!"/products/delete".equals(path)) {

            response.setStatus(
                    HttpServletResponse.SC_NOT_FOUND
            );

            response.getWriter().write(
                    "{\"error\":\"Admin endpoint not found\"}"
            );

            return;
        }

        try {

            String productIdValue =
                    request.getParameter("productId");

            if (productIdValue == null ||
                    productIdValue.trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "Product ID is required"
                );
            }

            Long productId =
                    Long.valueOf(
                            productIdValue
                    );

            adminService.removeProduct(
                    productId
            );

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            response.getWriter().write(
                    "{\"message\":\"Product removed successfully\"}"
            );

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
                            + escapeJson(
                            e.getMessage()
                    )
                            + "\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().write(
                    "{\"error\":\"Unable to remove product\"}"
            );
        }
    }

    private boolean authorizeAdmin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session =
                request.getSession(false);

        if (session == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    "{\"error\":\"Authentication required\"}"
            );

            return false;
        }

        Object userId =
                session.getAttribute("userId");

        Object role =
                session.getAttribute("userRole");

        if (userId == null ||
                role == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    "{\"error\":\"Invalid session\"}"
            );

            return false;
        }

        if (!"ADMIN".equals(
                role.toString())) {

            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    "{\"error\":\"Admin access required\"}"
            );

            return false;
        }

        return true;
    }

    private String buildUsersJson(
            List<User> users) {

        StringBuilder json =
                new StringBuilder(
                        "{\"users\":["
                );

        for (int i = 0;
             i < users.size();
             i++) {

            User user = users.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{")
                    .append("\"id\":")
                    .append(user.getId())
                    .append(",")

                    .append("\"name\":\"")
                    .append(
                            escapeJson(
                                    user.getName()
                            )
                    )
                    .append("\",")

                    .append("\"email\":\"")
                    .append(
                            escapeJson(
                                    user.getEmail()
                            )
                    )
                    .append("\",")

                    .append("\"role\":\"")
                    .append(
                            escapeJson(
                                    user.getRole()
                            )
                    )
                    .append("\",")

                    .append("\"createdAt\":\"")
                    .append(user.getCreatedAt())
                    .append("\"")
                    .append("}");
        }

        json.append("]}");

        return json.toString();
    }

    private String buildOrdersJson(
            List<AdminOrderView> orders) {

        StringBuilder json =
                new StringBuilder(
                        "{\"orders\":["
                );

        for (int i = 0;
             i < orders.size();
             i++) {

            AdminOrderView order =
                    orders.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{")
                    .append("\"orderId\":")
                    .append(order.getOrderId())
                    .append(",")

                    .append("\"buyerId\":")
                    .append(order.getBuyerId())
                    .append(",")

                    .append("\"buyerName\":\"")
                    .append(
                            escapeJson(
                                    order.getBuyerName()
                            )
                    )
                    .append("\",")

                    .append("\"status\":\"")
                    .append(
                            escapeJson(
                                    order.getStatus()
                            )
                    )
                    .append("\",")

                    .append("\"paymentStatus\":\"")
                    .append(
                            escapeJson(
                                    order.getPaymentStatus()
                            )
                    )
                    .append("\",")

                    .append("\"totalAmount\":")
                    .append(order.getTotalAmount())
                    .append(",")

                    .append("\"createdAt\":\"")
                    .append(order.getCreatedAt())
                    .append("\"")
                    .append("}");
        }

        json.append("]}");

        return json.toString();
    }

    private String buildProductsJson(
            List<Product> products) {

        StringBuilder json =
                new StringBuilder(
                        "{\"products\":["
                );

        for (int i = 0;
             i < products.size();
             i++) {

            Product product =
                    products.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{")
                    .append("\"id\":")
                    .append(product.getId())
                    .append(",")

                    .append("\"sellerId\":")
                    .append(product.getSellerId())
                    .append(",")

                    .append("\"name\":\"")
                    .append(
                            escapeJson(
                                    product.getName()
                            )
                    )
                    .append("\",")

                    .append("\"description\":\"")
                    .append(
                            escapeJson(
                                    product.getDescription()
                            )
                    )
                    .append("\",")

                    .append("\"price\":")
                    .append(product.getPrice())
                    .append(",")

                    .append("\"stock\":")
                    .append(product.getStock())
                    .append(",")
                    .append("\"active\":")
                    .append(product.getActive())
                    .append("}");
        }

        json.append("]}");

        return json.toString();
    }

    private String escapeJson(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}