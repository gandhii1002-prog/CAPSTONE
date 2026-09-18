package com.gandhimart.service;

import com.gandhimart.dao.CartDAO;
import com.gandhimart.dao.ProductDAO;
import com.gandhimart.dto.CartSummary;
import com.gandhimart.model.CartItem;
import com.gandhimart.model.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartService(
            CartDAO cartDAO,
            ProductDAO productDAO) {

        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    public CartSummary getCart(Long buyerId)
            throws SQLException {

        validateBuyer(buyerId);

        List<CartItem> items =
                cartDAO.findByBuyerId(buyerId);

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : items) {
            if (item.getSubtotal() != null) {
                total = total.add(item.getSubtotal());
            }
        }

        return new CartSummary(items, total);
    }

    public void addItem(
            Long buyerId,
            Long productId,
            Integer quantity) throws SQLException {

        validateBuyer(buyerId);
        validateProductId(productId);
        validateQuantity(quantity);

        Product product = getProduct(productId);

        validateStock(product, quantity);

        Optional<CartItem> existing =
                cartDAO.findItem(buyerId, productId);

        if (existing.isPresent()) {

            int newQuantity =
                    existing.get().getQuantity() + quantity;

            if (newQuantity > product.getStock()) {
                throw new IllegalArgumentException(
                        "Requested quantity exceeds available stock"
                );
            }
        }

        cartDAO.addItem(
                buyerId,
                productId,
                quantity
        );
    }

    public void updateQuantity(
            Long buyerId,
            Long productId,
            Integer quantity) throws SQLException {

        validateBuyer(buyerId);
        validateProductId(productId);
        validateQuantity(quantity);

        Product product = getProduct(productId);

        validateStock(product, quantity);

        Optional<CartItem> existing =
                cartDAO.findItem(buyerId, productId);

        if (existing.isEmpty()) {
            throw new IllegalArgumentException(
                    "Product is not in your cart"
            );
        }

        cartDAO.updateQuantity(
                buyerId,
                productId,
                quantity
        );
    }

    public void removeItem(
            Long buyerId,
            Long productId) throws SQLException {

        validateBuyer(buyerId);
        validateProductId(productId);

        Optional<CartItem> existing =
                cartDAO.findItem(buyerId, productId);

        if (existing.isEmpty()) {
            throw new IllegalArgumentException(
                    "Product is not in your cart"
            );
        }

        cartDAO.removeItem(
                buyerId,
                productId
        );
    }

    public void clearCart(Long buyerId)
            throws SQLException {

        validateBuyer(buyerId);
        cartDAO.clearCart(buyerId);
    }

    private Product getProduct(Long productId)
            throws SQLException {

        return productDAO.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found"
                        ));
    }

    private void validateBuyer(Long buyerId) {

        if (buyerId == null || buyerId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid buyer"
            );
        }
    }

    private void validateProductId(Long productId) {

        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product ID"
            );
        }
    }

    private void validateQuantity(Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }

    private void validateStock(
            Product product,
            Integer quantity) {

        if (product.getStock() == null ||
                product.getStock() <= 0) {

            throw new IllegalArgumentException(
                    "Product is out of stock"
            );
        }

        if (quantity > product.getStock()) {
            throw new IllegalArgumentException(
                    "Requested quantity exceeds available stock"
            );
        }
    }
}