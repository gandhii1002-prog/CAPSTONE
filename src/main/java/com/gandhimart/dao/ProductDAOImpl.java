package com.gandhimart.dao;

import com.gandhimart.model.Product;
import com.gandhimart.util.DatabaseConfig;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAOImpl implements ProductDAO {

    private final DataSource dataSource;

    public ProductDAOImpl() {
        this(DatabaseConfig.getDataSource());
    }

    public ProductDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void create(Product product) throws SQLException {

        String sql = """
                INSERT INTO products
                (seller_id, name, description, price, stock_quantity, category)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, product.getSellerId());
            statement.setString(2, product.getName());
            statement.setString(3, product.getDescription());
            statement.setBigDecimal(4, product.getPrice());
            statement.setInt(5, product.getStock());
            statement.setString(6, product.getCategory());

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
                    stock_quantity = ?,
                    category = ?,
                    updated_at = CURRENT_TIMESTAMP
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
            statement.setString(5, product.getCategory());
            statement.setLong(6, product.getId());
            statement.setLong(7, product.getSellerId());

            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Long productId, Long sellerId) throws SQLException {

                String sql = """
                                UPDATE products
                                SET active = FALSE
                                WHERE id = ?
                                    AND seller_id = ?
                                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, productId);
            statement.setLong(2, sellerId);

            int updated = statement.executeUpdate();

            if (updated != 1) {
                throw new SQLException("Product not found");
            }
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
                       stock_quantity,
                       category,
                       active,
                       created_at
                FROM products
                WHERE id = ?
                  AND active = TRUE
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
                       stock_quantity,
                       category,
                      active,
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

    @Override
    public List<Product> findAll() throws SQLException {

        String sql = """
                SELECT id,
                       seller_id,
                       name,
                       description,
                       price,
                       stock_quantity,
                       category,
                       active,
                       created_at
                FROM products
                  WHERE active = TRUE
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

        return search(
            keyword,
            null,
            minPrice,
            maxPrice,
            false,
            "NEWEST"
        );
        }

        @Override
        public List<Product> search(
            String keyword,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            boolean inStockOnly,
            String sort) throws SQLException {

        StringBuilder sql = new StringBuilder("""
                SELECT id,
                       seller_id,
                       name,
                       description,
                       price,
                       stock_quantity,
                   category,
                       active,
                       created_at
                FROM products
                WHERE active = TRUE
                """);

        List<Object> parameters = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND LOWER(name) LIKE ?");
            parameters.add(
                    "%" + keyword.trim().toLowerCase() + "%"
            );
        }

        if (category != null && !category.trim().isEmpty()) {
            sql.append(" AND LOWER(category) = ?");
            parameters.add(category.trim().toLowerCase());
        }

        if (minPrice != null) {
            sql.append(" AND price >= ?");
            parameters.add(minPrice);
        }

        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            parameters.add(maxPrice);
        }

        if (inStockOnly) {
            sql.append(" AND stock_quantity > 0");
        }

        switch (sort) {
            case "PRICE_ASC" -> sql.append(" ORDER BY price ASC, id DESC");
            case "PRICE_DESC" -> sql.append(" ORDER BY price DESC, id DESC");
            default -> sql.append(" ORDER BY created_at DESC, id DESC");
        }

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

    private Product mapProduct(ResultSet resultSet)
            throws SQLException {

        Product product = new Product();

        product.setId(resultSet.getLong("id"));
        product.setSellerId(
                resultSet.getLong("seller_id")
        );
        product.setName(
                resultSet.getString("name")
        );
        product.setDescription(
                resultSet.getString("description")
        );
        product.setPrice(
                resultSet.getBigDecimal("price")
        );
        product.setStock(
                resultSet.getInt("stock_quantity")
        );
        product.setCategory(
            resultSet.getString("category")
        );

        product.setActive(
            resultSet.getBoolean("active")
        );

        Timestamp timestamp =
                resultSet.getTimestamp("created_at");

        if (timestamp != null) {
            product.setCreatedAt(
                    timestamp.toLocalDateTime()
            );
        }

        return product;
    }
}