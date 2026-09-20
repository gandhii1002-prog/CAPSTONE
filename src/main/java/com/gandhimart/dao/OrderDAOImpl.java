package com.gandhimart.dao;

import com.gandhimart.dto.SellerOrderItem;
import com.gandhimart.model.Order;
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

public class OrderDAOImpl implements OrderDAO {

    private final DataSource dataSource;

    public OrderDAOImpl() {
                this(DatabaseConfig.getDataSource());
        }

        public OrderDAOImpl(DataSource dataSource) {
                this.dataSource = dataSource;
    }

    @Override
    public Long placeOrder(Long buyerId) throws SQLException {

        String cartSql = """
                SELECT
                    ci.product_id,
                    ci.quantity,
                    p.price,
                    p.stock_quantity
                FROM cart_items ci
                JOIN products p
                    ON p.id = ci.product_id
                WHERE ci.buyer_id = ?
                FOR UPDATE
                """;

        String orderSql = """
                INSERT INTO orders
                (buyer_id, total_amount, status, payment_status)
                VALUES (?, ?, ?, ?)
                """;

        String orderItemSql = """
                INSERT INTO order_items
                (order_id, product_id, quantity, unit_price)
                VALUES (?, ?, ?, ?)
                """;

        String stockSql = """
                UPDATE products
                SET stock_quantity = stock_quantity - ?
                WHERE id = ?
                  AND stock_quantity >= ?
                """;

        String clearCartSql = """
                DELETE FROM cart_items
                WHERE buyer_id = ?
                """;

        try (Connection connection =
                     dataSource.getConnection()) {

            connection.setAutoCommit(false);

            try {

                List<CartRow> cartRows = new ArrayList<>();
                BigDecimal total = BigDecimal.ZERO;

                try (PreparedStatement statement =
                             connection.prepareStatement(cartSql)) {

                    statement.setLong(1, buyerId);

                    try (ResultSet resultSet =
                                 statement.executeQuery()) {

                        while (resultSet.next()) {

                            long productId =
                                    resultSet.getLong("product_id");

                            int quantity =
                                    resultSet.getInt("quantity");

                            BigDecimal price =
                                    resultSet.getBigDecimal("price");

                            int stockQuantity =
                                    resultSet.getInt("stock_quantity");

                            if (quantity <= 0) {
                                throw new IllegalArgumentException(
                                        "Invalid cart quantity"
                                );
                            }

                            if (stockQuantity < quantity) {
                                throw new IllegalArgumentException(
                                        "Insufficient stock for one or more products"
                                );
                            }

                            cartRows.add(
                                    new CartRow(
                                            productId,
                                            quantity,
                                            price
                                    )
                            );

                            total = total.add(
                                    price.multiply(
                                            BigDecimal.valueOf(quantity)
                                    )
                            );
                        }
                    }
                }

                if (cartRows.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Cart is empty"
                    );
                }

                Long orderId;

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     orderSql,
                                     java.sql.Statement.RETURN_GENERATED_KEYS
                             )) {

                    statement.setLong(1, buyerId);
                    statement.setBigDecimal(2, total);
                    statement.setString(3, "CONFIRMED");
                    statement.setString(4, "PAID");

                    statement.executeUpdate();

                    try (ResultSet keys =
                                 statement.getGeneratedKeys()) {

                        if (!keys.next()) {
                            throw new SQLException(
                                    "Unable to create order"
                            );
                        }

                        orderId = keys.getLong(1);
                    }
                }

                try (PreparedStatement itemStatement =
                             connection.prepareStatement(orderItemSql);
                     PreparedStatement stockStatement =
                             connection.prepareStatement(stockSql)) {

                    for (CartRow row : cartRows) {

                        itemStatement.setLong(
                                1,
                                orderId
                        );

                        itemStatement.setLong(
                                2,
                                row.productId()
                        );

                        itemStatement.setInt(
                                3,
                                row.quantity()
                        );

                        itemStatement.setBigDecimal(
                                4,
                                row.unitPrice()
                        );

                        itemStatement.addBatch();

                        stockStatement.setInt(
                                1,
                                row.quantity()
                        );

                        stockStatement.setLong(
                                2,
                                row.productId()
                        );

                        stockStatement.setInt(
                                3,
                                row.quantity()
                        );

                        int updated =
                                stockStatement.executeUpdate();

                        if (updated != 1) {
                            throw new IllegalArgumentException(
                                    "Insufficient stock"
                            );
                        }
                    }

                    itemStatement.executeBatch();
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     clearCartSql
                             )) {

                    statement.setLong(1, buyerId);
                    statement.executeUpdate();
                }

                connection.commit();

                return orderId;

            } catch (Exception e) {

                connection.rollback();

                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }

                throw e;

            } finally {

                connection.setAutoCommit(true);
            }
        }
    }

    @Override
    public Optional<Order> findById(Long orderId)
            throws SQLException {

        String sql = """
                SELECT
                    id,
                    buyer_id,
                    status,
                    total_amount,
                    created_at
                FROM orders
                WHERE id = ?
                """;

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, orderId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    Order order = new Order();

                    order.setId(
                            resultSet.getLong("id")
                    );

                    order.setBuyerId(
                            resultSet.getLong("buyer_id")
                    );

                    order.setStatus(
                            resultSet.getString("status")
                    );

                    order.setTotalAmount(
                            resultSet.getBigDecimal(
                                    "total_amount"
                            )
                    );

                    Timestamp timestamp =
                            resultSet.getTimestamp(
                                    "created_at"
                            );

                    if (timestamp != null) {
                        order.setCreatedAt(
                                timestamp.toLocalDateTime()
                        );
                    }

                    return Optional.of(order);
                }
            }
        }

        return Optional.empty();
    }

        @Override
        public List<Order> findByBuyerId(Long buyerId)
                        throws SQLException {

                String sql = """
                                SELECT
                                        id,
                                        buyer_id,
                                        status,
                                        total_amount,
                                        created_at
                                FROM orders
                                WHERE buyer_id = ?
                                ORDER BY created_at DESC
                                """;

                List<Order> orders = new ArrayList<>();

                try (Connection connection =
                                         dataSource.getConnection();
                         PreparedStatement statement =
                                         connection.prepareStatement(sql)) {

                        statement.setLong(1, buyerId);

                        try (ResultSet resultSet =
                                                 statement.executeQuery()) {

                                while (resultSet.next()) {
                                        orders.add(
                                                        mapOrder(resultSet)
                                        );
                                }
                        }
                }

                return orders;
        }

    @Override
    public List<SellerOrderItem> findIncomingBySellerId(
            Long sellerId) throws SQLException {

        String sql = """
                SELECT
                    o.id AS order_id,
                    o.buyer_id,
                    u.name AS buyer_name,
                    o.status,
                    o.payment_status,
                    oi.product_id,
                    p.name AS product_name,
                    oi.quantity,
                    oi.unit_price,
                    (oi.quantity * oi.unit_price) AS subtotal,
                    o.total_amount AS order_total,
                    o.created_at
                FROM orders o
                JOIN users u
                    ON u.id = o.buyer_id
                JOIN order_items oi
                    ON oi.order_id = o.id
                JOIN products p
                    ON p.id = oi.product_id
                WHERE p.seller_id = ?
                ORDER BY o.created_at DESC,
                         o.id DESC
                """;

        List<SellerOrderItem> orders =
                new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, sellerId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    SellerOrderItem item =
                            new SellerOrderItem();

                    item.setOrderId(
                            resultSet.getLong("order_id")
                    );

                    item.setBuyerId(
                            resultSet.getLong("buyer_id")
                    );

                    item.setBuyerName(
                            resultSet.getString("buyer_name")
                    );

                    item.setStatus(
                            resultSet.getString("status")
                    );

                    item.setPaymentStatus(
                            resultSet.getString("payment_status")
                    );

                    item.setProductId(
                            resultSet.getLong("product_id")
                    );

                    item.setProductName(
                            resultSet.getString("product_name")
                    );

                    item.setQuantity(
                            resultSet.getInt("quantity")
                    );

                    item.setUnitPrice(
                            resultSet.getBigDecimal("unit_price")
                    );

                    item.setSubtotal(
                            resultSet.getBigDecimal("subtotal")
                    );

                    item.setOrderTotal(
                            resultSet.getBigDecimal("order_total")
                    );

                    Timestamp timestamp =
                            resultSet.getTimestamp("created_at");

                    if (timestamp != null) {
                        item.setCreatedAt(
                                timestamp.toLocalDateTime()
                        );
                    }

                    orders.add(item);
                }
            }
        }

        return orders;
    }

    private Order mapOrder(ResultSet resultSet)
            throws SQLException {

        Order order = new Order();

        order.setId(
                resultSet.getLong("id")
        );

        order.setBuyerId(
                resultSet.getLong("buyer_id")
        );

        order.setStatus(
                resultSet.getString("status")
        );

        order.setTotalAmount(
                resultSet.getBigDecimal("total_amount")
        );

        Timestamp timestamp =
                resultSet.getTimestamp("created_at");

        if (timestamp != null) {
            order.setCreatedAt(
                    timestamp.toLocalDateTime()
            );
        }

        return order;
    }

    private record CartRow(
            Long productId,
            Integer quantity,
            BigDecimal unitPrice
    ) {
    }
}