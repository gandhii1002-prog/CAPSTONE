package com.gandhimart.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.File;

public class DatabaseConfig {

    private static HikariDataSource dataSource;

    private DatabaseConfig() {
    }

    public static synchronized void initialize() {

        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        try {
            // Store the H2 database in the user's home directory.
            // This avoids the previous "/db: Read-only file system" problem.
            String dbDirectory =
                    System.getProperty("user.home") + File.separator + "gandhimart-db";

            File directory = new File(dbDirectory);

            if (!directory.exists() && !directory.mkdirs()) {
                throw new RuntimeException(
                        "Could not create database directory: " + dbDirectory
                );
            }

            String jdbcUrl =
                    "jdbc:h2:file:"
                    + dbDirectory
                    + File.separator
                    + "gandhimart"
                    + ";AUTO_SERVER=TRUE";

            HikariConfig config = new HikariConfig();

            config.setJdbcUrl(jdbcUrl);
            config.setUsername("sa");
            config.setPassword("");
            config.setDriverClassName("org.h2.Driver");

            config.setMaximumPoolSize(5);
            config.setMinimumIdle(1);

            dataSource = new HikariDataSource(config);

            System.out.println("========================================");
            System.out.println("GandhiMart database initialized");
            System.out.println("Database URL: " + jdbcUrl);
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("Failed to initialize GandhiMart database.");
            e.printStackTrace();

            throw new RuntimeException(
                    "Database initialization failed.",
                    e
            );
        }
    }

    public static DataSource getDataSource() {

        if (dataSource == null || dataSource.isClosed()) {
            throw new IllegalStateException(
                    "Database has not been initialized."
            );
        }

        return dataSource;
    }

    public static void close() {

        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;

            System.out.println("GandhiMart database connection closed.");
        }
    }
}