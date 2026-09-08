package com.gandhimart.service;

import com.gandhimart.dao.ProductDAO;
import com.gandhimart.model.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductService {

    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public void createProduct(
            Long sellerId,
            String name,
            String description,
            BigDecimal price,
            Integer stock) throws SQLException {

        validateSeller(sellerId);
        validateProduct(name, description, price, stock);

        Product product = new Product();

        product.setSellerId(sellerId);
        product.setName(name.trim());
        product.setDescription(
                description == null ? "" : description.trim()
        );
        product.setPrice(price);
        product.setStock(stock);

        productDAO.create(product);
    }

    public void updateProduct(
            Long sellerId,
            Long productId,
            String name,
            String description,
            BigDecimal price,
            Integer stock) throws SQLException {

        validateSeller(sellerId);

        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID");
        }

        validateProduct(name, description, price, stock);

        Optional<Product> existing =
                productDAO.findById(productId);

        if (existing.isEmpty()) {
            throw new IllegalArgumentException(
                    "Product not found"
            );
        }

        if (!sellerId.equals(existing.get().getSellerId())) {
            throw new IllegalArgumentException(
                    "You are not allowed to modify this product"
            );
        }

        Product product = existing.get();

        product.setName(name.trim());
        product.setDescription(
                description == null ? "" : description.trim()
        );
        product.setPrice(price);
        product.setStock(stock);
        product.setSellerId(sellerId);

        productDAO.update(product);
    }

    public void deleteProduct(
            Long sellerId,
            Long productId) throws SQLException {

        validateSeller(sellerId);

        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product ID"
            );
        }

        Optional<Product> existing =
                productDAO.findById(productId);

        if (existing.isEmpty()) {
            throw new IllegalArgumentException(
                    "Product not found"
            );
        }

        if (!sellerId.equals(existing.get().getSellerId())) {
            throw new IllegalArgumentException(
                    "You are not allowed to delete this product"
            );
        }

        productDAO.delete(productId, sellerId);
    }

    public Optional<Product> getProduct(Long productId)
            throws SQLException {

        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product ID"
            );
        }

        return productDAO.findById(productId);
    }

    public List<Product> getSellerProducts(Long sellerId)
            throws SQLException {

        validateSeller(sellerId);

        return productDAO.findBySellerId(sellerId);
    }

    private void validateSeller(Long sellerId) {

        if (sellerId == null || sellerId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid seller"
            );
        }
    }

    private void validateProduct(
            String name,
            String description,
            BigDecimal price,
            Integer stock) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Product name is required"
            );
        }

        if (name.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "Product name is too long"
            );
        }

        if (description != null &&
                description.length() > 1000) {
            throw new IllegalArgumentException(
                    "Product description is too long"
            );
        }

        if (price == null ||
                price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Price must be greater than zero"
            );
        }

        if (stock == null || stock < 0) {
            throw new IllegalArgumentException(
                    "Stock cannot be negative"
            );
        }
    }
}