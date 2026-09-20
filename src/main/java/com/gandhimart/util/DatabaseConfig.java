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
            String dbDirectory =
                    resolveDatabaseDirectory();

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
                config.setUsername(
                    System.getenv()
                        .getOrDefault(
                            "GANDHIMART_DB_USER",
                            "sa"
                        )
                );
                config.setPassword(
                    System.getenv()
                        .getOrDefault(
                            "GANDHIMART_DB_PASSWORD",
                            ""
                        )
                );
            config.setDriverClassName("org.h2.Driver");

            config.setMaximumPoolSize(5);
            config.setMinimumIdle(1);

            dataSource = new HikariDataSource(config);

            System.out.println("GandhiMart database initialized.");
            System.out.println("Database directory: " + dbDirectory);

        } catch (Exception e) {
            System.err.println("Failed to initialize GandhiMart database.");
            throw new RuntimeException(
                    "Database initialization failed.",
                    e
            );
        }
    }

        private static String resolveDatabaseDirectory() {

        String configuredDirectory =
            System.getenv("GANDHIMART_DB_DIR");

        if (configuredDirectory != null &&
            !configuredDirectory.trim().isEmpty()) {

            return configuredDirectory.trim();
        }

        String railwayVolumePath =
            System.getenv("RAILWAY_VOLUME_MOUNT_PATH");

        if (railwayVolumePath != null &&
            !railwayVolumePath.trim().isEmpty()) {

            return railwayVolumePath.trim()
                + File.separator
                + "gandhimart-db";
        }

        return System.getProperty("user.home")
            + File.separator
            + "gandhimart-db";
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