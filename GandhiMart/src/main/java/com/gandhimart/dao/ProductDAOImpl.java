package com.gandhimart.dao;

import com.gandhimart.model.Product;
import com.gandhimart.util.DatabaseConfig;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class ProductDAOImpl implements ProductDAO {

    private final DataSource dataSource;

    public ProductDAOImpl() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    @Override
    public void create(Product product) throws SQLException {

        String sql = """
                INSERT INTO products
                (seller_id, name, description, price, stock)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, product.getSellerId());
            statement.setString(2, product.getName());
            statement.setString(3, product.getDescription());
            statement.setBigDecimal(4, product.getPrice());
            statement.setInt(5, product.getStock());

            statement.executeUpdate();
        }
    }

    @Override
    public void update(Product product) throws SQLException {

        String sql = """
                UPDATE products
                SET name = ?,
                    description = ?,
                    price = ?,
                    stock = ?
                WHERE id = ?
                  AND seller_id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setBigDecimal(3, product.getPrice());
            statement.setInt(4, product.getStock());
            statement.setLong(5, product.getId());
            statement.setLong(6, product.getSellerId());

            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Long productId, Long sellerId) throws SQLException {

        String sql = """
                DELETE FROM products
                WHERE id = ?
                  AND seller_id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, productId);
            statement.setLong(2, sellerId);

            statement.executeUpdate();
        }
    }

    @Override
    public Optional<Product> findById(Long productId)
            throws SQLException {

        String sql = """
                SELECT id,
                       seller_id,
                       name,
                       description,
                       price,
                       stock,
                       created_at
                FROM products
                WHERE id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, productId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapProduct(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Product> findBySellerId(Long sellerId)
            throws SQLException {

        String sql = """
                SELECT id,
                       seller_id,
                       name,
                       description,
                       price,
                       stock,
                       created_at
                FROM products
                WHERE seller_id = ?
                ORDER BY created_at DESC
                """;

        List<Product> products = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, sellerId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    products.add(mapProduct(resultSet));
                }
            }
        }

        return products;
    }

    private Product mapProduct(ResultSet resultSet)
            throws SQLException {

        Product product = new Product();

        product.setId(resultSet.getLong("id"));
        product.setSellerId(resultSet.getLong("seller_id"));
        product.setName(resultSet.getString("name"));
        product.setDescription(resultSet.getString("description"));
        product.setPrice(resultSet.getBigDecimal("price"));
        product.setStock(resultSet.getInt("stock"));

        Timestamp timestamp =
                resultSet.getTimestamp("created_at");

        if (timestamp != null) {
            product.setCreatedAt(
                    timestamp.toLocalDateTime()
            );
        }

        return product;
    }

    @Override
public List<Product> findAll() throws SQLException {

    String sql = """
            SELECT id,
                   seller_id,
                   name,
                   description,
                   price,
                   stock,
                   created_at
            FROM products
            ORDER BY created_at DESC
            """;

    List<Product> products = new ArrayList<>();

    try (Connection connection = dataSource.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        while (resultSet.next()) {
            products.add(mapProduct(resultSet));
        }
    }

    return products;
}

@Override
public List<Product> search(
        String keyword,
        BigDecimal minPrice,
        BigDecimal maxPrice) throws SQLException {

    StringBuilder sql = new StringBuilder("""
            SELECT id,
                   seller_id,
                   name,
                   description,
                   price,
                   stock,
                   created_at
            FROM products
            WHERE 1 = 1
            """);

    List<Object> parameters = new ArrayList<>();

    if (keyword != null && !keyword.trim().isEmpty()) {
        sql.append(" AND LOWER(name) LIKE ?");
        parameters.add("%" + keyword.trim().toLowerCase() + "%");
    }

    if (minPrice != null) {
        sql.append(" AND price >= ?");
        parameters.add(minPrice);
    }

    if (maxPrice != null) {
        sql.append(" AND price <= ?");
        parameters.add(maxPrice);
    }

    sql.append(" ORDER BY created_at DESC");

    List<Product> products = new ArrayList<>();

    try (Connection connection = dataSource.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql.toString())) {

        for (int i = 0; i < parameters.size(); i++) {
            statement.setObject(i + 1, parameters.get(i));
        }

        try (ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                products.add(mapProduct(resultSet));
            }
        }
    }

    return products;
}
}