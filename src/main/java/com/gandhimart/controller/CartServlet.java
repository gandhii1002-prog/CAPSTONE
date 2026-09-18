package com.gandhimart.controller;

import com.gandhimart.dao.CartDAOImpl;
import com.gandhimart.dao.ProductDAOImpl;
import com.gandhimart.dto.CartSummary;
import com.gandhimart.model.CartItem;
import com.gandhimart.service.CartService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/v1/cart/*")
public class CartServlet extends HttpServlet {

    private CartService cartService;

    @Override
    public void init() {

        cartService = new CartService(
                new CartDAOImpl(),
                new ProductDAOImpl()
        );
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Long buyerId = getBuyerId(request, response);

        if (buyerId == null) {
            return;
        }

        try {

            CartSummary summary =
                    cartService.getCart(buyerId);

            response.setStatus(
                    HttpServletResponse.SC_OK
            );

            response.getWriter().write(
                    buildJson(summary)
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().write(
                    "{\"error\":\"Unable to load cart\"}"
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Long buyerId = getBuyerId(request, response);

        if (buyerId == null) {
            return;
        }

        String path = request.getPathInfo();

        if (path == null) {
            path = "";
        }

        try {

            switch (path) {

                case "/add":
                    addItem(request, response, buyerId);
                    break;

                case "/update":
                    updateItem(request, response, buyerId);
                    break;

                case "/remove":
                    removeItem(request, response, buyerId);
                    break;

                case "/clear":
                    clearCart(response, buyerId);
                    break;

                default:
                    response.setStatus(
                            HttpServletResponse.SC_NOT_FOUND
                    );

                    response.getWriter().write(
                            "{\"error\":\"Cart operation not found\"}"
                    );
            }

        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().write(
                    "{\"error\":\"Invalid numeric value\"}"
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
                    "{\"error\":\"Cart operation failed\"}"
            );
        }
    }

    private void addItem(
            HttpServletRequest request,
            HttpServletResponse response,
            Long buyerId) throws Exception {

        Long productId =
                Long.valueOf(
                        request.getParameter("productId")
                );

        Integer quantity =
                Integer.valueOf(
                        request.getParameter("quantity")
                );

        cartService.addItem(
                buyerId,
                productId,
                quantity
        );

        response.setStatus(
                HttpServletResponse.SC_CREATED
        );

        response.getWriter().write(
                "{\"message\":\"Product added to cart\"}"
        );
    }

    private void updateItem(
            HttpServletRequest request,
            HttpServletResponse response,
            Long buyerId) throws Exception {

        Long productId =
                Long.valueOf(
                        request.getParameter("productId")
                );

        Integer quantity =
                Integer.valueOf(
                        request.getParameter("quantity")
                );

        cartService.updateQuantity(
                buyerId,
                productId,
                quantity
        );

        response.setStatus(
                HttpServletResponse.SC_OK
        );

        response.getWriter().write(
                "{\"message\":\"Cart quantity updated\"}"
        );
    }

    private void removeItem(
            HttpServletRequest request,
            HttpServletResponse response,
            Long buyerId) throws Exception {

        Long productId =
                Long.valueOf(
                        request.getParameter("productId")
                );

        cartService.removeItem(
                buyerId,
                productId
        );

        response.setStatus(
                HttpServletResponse.SC_OK
        );

        response.getWriter().write(
                "{\"message\":\"Product removed from cart\"}"
        );
    }

    private void clearCart(
            HttpServletResponse response,
            Long buyerId) throws Exception {

        cartService.clearCart(buyerId);

        response.setStatus(
                HttpServletResponse.SC_OK
        );

        response.getWriter().write(
                "{\"message\":\"Cart cleared\"}"
        );
    }

    private Long getBuyerId(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session =
                request.getSession(false);

        if (session == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().write(
                    "{\"error\":\"Authentication required\"}"
            );

            return null;
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

            return null;
        }

        String role =
                roleObject.toString();

        if (!"BUYER".equals(role)) {

            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );

            response.getWriter().write(
                    "{\"error\":\"Only buyers can use the cart\"}"
            );

            return null;
        }

        try {

            return Long.valueOf(
                    userIdObject.toString()
            );

        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().write(
                    "{\"error\":\"Invalid user session\"}"
            );

            return null;
        }
    }

    private String buildJson(
            CartSummary summary) {

        StringBuilder json =
                new StringBuilder();

        json.append("{\"items\":[");

        List<CartItem> items =
                summary.getItems();

        for (int i = 0; i < items.size(); i++) {

            CartItem item = items.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{")
                    .append("\"id\":")
                    .append(item.getId())
                    .append(",")

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

                    .append("\"productPrice\":")
                    .append(
                            item.getProductPrice()
                    )
                    .append(",")

                    .append("\"quantity\":")
                    .append(item.getQuantity())
                    .append(",")

                    .append("\"subtotal\":")
                    .append(item.getSubtotal())
                    .append("}");
        }

        json.append("],");

        json.append("\"total\":")
                .append(summary.getTotal());

        json.append("}");

        return json.toString();
    }

    private String escapeJson(
            String value) {

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