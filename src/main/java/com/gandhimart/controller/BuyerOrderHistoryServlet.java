package com.gandhimart.controller;

import com.gandhimart.dao.OrderDAOImpl;
import com.gandhimart.model.Order;
import com.gandhimart.service.OrderService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/v1/orders/my")
public class BuyerOrderHistoryServlet extends HttpServlet {

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

        if (!"BUYER".equals(
                roleObject.toString())) {

            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );

            response.getWriter().write(
                    "{\"error\":\"Only buyers can view buyer order history\"}"
            );

            return;
        }

        try {

            Long buyerId =
                    Long.valueOf(
                            userIdObject.toString()
                    );

            List<Order> orders =
                    orderService.getBuyerOrders(
                            buyerId
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
                    "{\"error\":\"Unable to load order history\"}"
            );
        }
    }

    private String buildJson(
            List<Order> orders) {

        StringBuilder json =
                new StringBuilder();

        json.append("{\"orders\":[");

        for (int i = 0;
             i < orders.size();
             i++) {

            Order order = orders.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{")
                    .append("\"id\":")
                    .append(order.getId())
                    .append(",")

                    .append("\"status\":\"")
                    .append(
                            escapeJson(
                                    order.getStatus()
                            )
                    )
                    .append("\",")

                    .append("\"totalAmount\":")
                    .append(
                            order.getTotalAmount()
                    )
                    .append(",")

                    .append("\"createdAt\":\"")
                    .append(
                            order.getCreatedAt()
                    )
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