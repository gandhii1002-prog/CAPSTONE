package com.gandhimart.dao;

import com.gandhimart.model.Product;
import com.gandhimart.testutil.H2TestDatabase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductDAOImplTest {

    private DataSource dataSource;
    private ProductDAOImpl productDAO;

    @BeforeEach
    void setUp() throws Exception {

        dataSource =
                H2TestDatabase.create();

        productDAO =
                new ProductDAOImpl(
                        dataSource
                );

        insertSeller();
    }

    private void insertSeller()
            throws Exception {

        try (var connection =
                     dataSource.getConnection();
             var statement =
                     connection.prepareStatement(
                             """
                             INSERT INTO users
                             (name, email, password_hash, role)
                             VALUES (?, ?, ?, ?)
                             """
                     )) {

            statement.setString(
                    1,
                    "Test Seller"
            );

            statement.setString(
                    2,
                    "seller@test.com"
            );

            statement.setString(
                    3,
                    "bcrypt-hash"
            );

            statement.setString(
                    4,
                    "SELLER"
            );

            statement.executeUpdate();
        }
    }

    @Test
    void createAndFindById()
            throws Exception {

        Product product =
                new Product();

        product.setSellerId(1L);
        product.setName(
                "Test Laptop"
        );
        product.setDescription(
                "DAO integration test"
        );
        product.setPrice(
                new BigDecimal("50000.00")
        );
        product.setStock(10);

        productDAO.create(product);

        List<Product> products =
                productDAO.findAll();

        assertEquals(
                1,
                products.size()
        );

        Product saved =
                products.get(0);

        assertEquals(
                "Test Laptop",
                saved.getName()
        );

        assertEquals(
                10,
                saved.getStock()
        );

        Optional<Product> result =
                productDAO.findById(
                        saved.getId()
                );

        assertTrue(result.isPresent());

        assertEquals(
                "Test Laptop",
                result.get().getName()
        );
    }

    @Test
    void searchShouldFindProduct()
            throws Exception {

        Product product =
                new Product();

        product.setSellerId(1L);
        product.setName(
                "Gaming Laptop"
        );
        product.setDescription(
                "Gaming computer"
        );
        product.setPrice(
                new BigDecimal("75000.00")
        );
        product.setStock(5);

        productDAO.create(product);

        List<Product> results =
                productDAO.search(
                        "gaming",
                        null,
                        null
                );

        assertEquals(
                1,
                results.size()
        );

        assertEquals(
                "Gaming Laptop",
                results.get(0).getName()
        );
    }

        @Test
        void searchShouldFilterCategoryStockAndSortByPrice()
                        throws Exception {

                Product inStock = new Product();
                inStock.setSellerId(1L);
                inStock.setName("Fresh Apples");
                inStock.setCategory("Groceries");
                inStock.setPrice(new BigDecimal("10.00"));
                inStock.setStock(5);
                productDAO.create(inStock);

                Product outOfStock = new Product();
                outOfStock.setSellerId(1L);
                outOfStock.setName("Fresh Bananas");
                outOfStock.setCategory("Groceries");
                outOfStock.setPrice(new BigDecimal("5.00"));
                outOfStock.setStock(0);
                productDAO.create(outOfStock);

                List<Product> results = productDAO.search(
                                "fresh",
                                "groceries",
                                null,
                                null,
                                true,
                                "PRICE_ASC"
                );

                assertEquals(1, results.size());
                assertEquals("Fresh Apples", results.get(0).getName());
                assertEquals("Groceries", results.get(0).getCategory());
        }

    @Test
    void inactiveProductShouldNotAppearInBuyerListing()
            throws Exception {

        Product product =
                new Product();

        product.setSellerId(1L);
        product.setName(
                "Inactive Product"
        );
        product.setDescription(
                "Should be hidden"
        );
        product.setPrice(
                new BigDecimal("1000.00")
        );
        product.setStock(5);

        productDAO.create(product);

        List<Product> products =
                productDAO.findAll();

        assertEquals(
                1,
                products.size()
        );

        productDAO.delete(
                products.get(0).getId(),
                1L
        );

        assertTrue(
                productDAO.findAll().isEmpty()
        );
    }
}