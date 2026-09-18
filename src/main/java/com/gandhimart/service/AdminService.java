package com.gandhimart.service;

import com.gandhimart.dao.AdminDAO;
import com.gandhimart.dto.AdminOrderView;
import com.gandhimart.model.Product;
import com.gandhimart.model.User;

import java.sql.SQLException;
import java.util.List;

public class AdminService {

    private final AdminDAO adminDAO;

    public AdminService(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    public List<User> getAllUsers()
            throws SQLException {

        return adminDAO.findAllUsers();
    }

    public List<AdminOrderView> getAllOrders()
            throws SQLException {

        return adminDAO.findAllOrders();
    }

    public List<Product> getAllProducts()
            throws SQLException {

        return adminDAO.findAllProducts();
    }

    public void removeProduct(Long productId)
            throws SQLException {

        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product ID"
            );
        }

        adminDAO.deleteProduct(productId);
    }
}