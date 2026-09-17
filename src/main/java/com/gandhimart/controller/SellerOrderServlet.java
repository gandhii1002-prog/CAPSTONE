package com.gandhimart.controller;

import com.gandhimart.dao.OrderDAOImpl;
import com.gandhimart.dto.SellerOrderItem;
import com.gandhimart.service.OrderService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/v1/orders/seller")
public class SellerOrderServlet extends HttpServlet {

    private OrderService orderService;

    @Override
    public void init() {

        orderService =
                new OrderService(
                        new OrderDAOImpl()
                );
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType(
                "application/json"
        );

        response.setCharacterEncoding(
                "UTF-8"
        );

        HttpSession session =
                request.getSession(false);

        if (session == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().write(
                    "{\"error\":\"Authentication required\"}"
            );

            return;
        }

        Object userIdObject =
                session.getAttribute("userId");

        Object roleObject =
                session.getAttribute("userRole");

        if (userIdObject == null ||
                roleObject == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().write(
                    "{\"error\":\"Invalid session\"}"
            );

            return;
        }

        if (!"SELLER".equals(
                roleObject.toString())) {

            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );

            response.getWriter().write(
                    "{\"error\":\"Only sellers can view incoming orders\"}"
            );

            return;
        }

        try {

            Long sellerId =
                    Long.valueOf(
                            userIdObject.toString()
                    );

            List<SellerOrderItem> orders =
                    orderService.getSellerOrders(
                            sellerId
                    );

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            response.getWriter().write(
                    buildJson(orders)
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().write(
                    "{\"error\":\"Unable to load seller orders\"}"
            );
        }
    }

    private String buildJson(
            List<SellerOrderItem> orders) {

        StringBuilder json =
                new StringBuilder();

        json.append("{\"orders\":[");

        for (int i = 0;
             i < orders.size();
             i++) {

            SellerOrderItem item =
                    orders.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{")
                    .append("\"orderId\":")
                    .append(item.getOrderId())
                    .append(",")

                    .append("\"buyerId\":")
                    .append(item.getBuyerId())
                    .append(",")

                    .append("\"buyerName\":\"")
                    .append(
                            escapeJson(
                                    item.getBuyerName()
                            )
                    )
                    .append("\",")

                    .append("\"status\":\"")
                    .append(
                            escapeJson(
                                    item.getStatus()
                            )
                    )
                    .append("\",")

                    .append("\"paymentStatus\":\"")
                    .append(
                            escapeJson(
                                    item.getPaymentStatus()
                            )
                    )
                    .append("\",")

                    .append("\"productId\":")
                    .append(item.getProductId())
                    .append(",")

                    .append("\"productName\":\"")
                    .append(
                            escapeJson(
                                    item.getProductName()
                            )
                    )
                    .append("\",")

                    .append("\"quantity\":")
                    .append(item.getQuantity())
                    .append(",")

                    .append("\"unitPrice\":")
                    .append(item.getUnitPrice())
                    .append(",")

                    .append("\"subtotal\":")
                    .append(item.getSubtotal())
                    .append(",")

                    .append("\"orderTotal\":")
                    .append(item.getOrderTotal())
                    .append(",")

                    .append("\"createdAt\":\"")
                    .append(item.getCreatedAt())
                    .append("\"")
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