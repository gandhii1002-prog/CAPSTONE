package com.gandhimart.dao;

import com.gandhimart.dto.AdminOrderView;
import com.gandhimart.model.Product;
import com.gandhimart.model.User;

import java.sql.SQLException;
import java.util.List;

public interface AdminDAO {

    List<User> findAllUsers() throws SQLException;

    List<AdminOrderView> findAllOrders() throws SQLException;

    List<Product> findAllProducts() throws SQLException;

    void deleteProduct(Long productId) throws SQLException;
}