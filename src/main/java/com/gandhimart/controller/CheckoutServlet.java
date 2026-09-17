package com.gandhimart.controller;

import com.gandhimart.dao.OrderDAOImpl;
import com.gandhimart.service.OrderService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/v1/checkout")
public class CheckoutServlet extends HttpServlet {

    private OrderService orderService;

    @Override
    public void init() {

        orderService =
                new OrderService(
                        new OrderDAOImpl()
                );
    }

    @Override
    protected void doPost(
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
                    "{\"error\":\"Only buyers can checkout\"}"
            );

            return;
        }

        try {

            Long buyerId =
                    Long.valueOf(
                            userIdObject.toString()
                    );

            String paymentConfirmed =
                    request.getParameter(
                            "paymentConfirmed"
                    );

            boolean confirmed =
                    "true".equalsIgnoreCase(
                            paymentConfirmed
                    );

            Long orderId =
                    orderService.checkout(
                            buyerId,
                            confirmed
                    );

            response.setStatus(
                    HttpServletResponse.SC_CREATED
            );

            response.getWriter().write(
                    "{"
                            + "\"message\":\"Order placed successfully\","
                            + "\"orderId\":"
                            + orderId
                            + "}"
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
                    "{\"error\":\"Checkout failed\"}"
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