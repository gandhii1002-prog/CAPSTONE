package com.gandhimart.dao;

import com.gandhimart.model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {

    void create(Product product) throws SQLException;

    void update(Product product) throws SQLException;

    void delete(Long productId, Long sellerId) throws SQLException;

    Optional<Product> findById(Long productId) throws SQLException;

    List<Product> findBySellerId(Long sellerId) throws SQLException;
}