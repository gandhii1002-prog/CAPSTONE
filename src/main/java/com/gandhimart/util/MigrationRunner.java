package com.gandhimart.util;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.Collectors;

public class MigrationRunner {

    private MigrationRunner() {
    }

    public static void runMigrations() {
        runMigrations(DatabaseConfig.getDataSource());
    }

    public static void runMigrations(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            createSchemaVersionTable(connection);

            runMigration(
                    connection,
                    "V1",
                    "Initial GandhiMart database schema",
                    "db/migrations/V1__init_schema.sql"
            );

            runMigration(
                    connection,
                    "V2",
                    "Add active status to products",
                    "db/migrations/V2__add_product_active_status.sql"
            );

                runMigration(
                    connection,
                    "V3",
                    "Add product reviews and ratings",
                    "db/migrations/V3__add_reviews_table.sql"
                    );

                    runMigration(
                        connection,
                        "V4",
                        "Add database hardening and browse indexes",
                        "db/migrations/V4__harden_browse_indexes.sql"
                    );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Database migration failed.",
                    e
            );
        }
    }

    private static void createSchemaVersionTable(
            Connection connection) throws Exception {

        String sql = """
                CREATE TABLE IF NOT EXISTS schema_version (
                    version VARCHAR(50) PRIMARY KEY,
                    description VARCHAR(255),
                    installed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(sql)) {

            preparedStatement.execute();
        }
    }

    private static void runMigration(
            Connection connection,
            String version,
            String description,
            String resourcePath) throws Exception {

        if (isMigrationApplied(connection, version)) {
            System.out.println(
                    "Migration " + version + " already applied. Skipping."
            );
            return;
        }

        System.out.println(
                "Running database migration " + version + "..."
        );

        String sql = loadMigration(resourcePath);

        executeSqlScript(connection, sql);

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(
                             """
                             INSERT INTO schema_version
                             (version, description)
                             VALUES (?, ?)
                             """
                     )) {

            preparedStatement.setString(1, version);
            preparedStatement.setString(2, description);
            preparedStatement.executeUpdate();
        }

        System.out.println(
                "Migration " + version + " completed successfully."
        );
    }

    private static String loadMigration(
            String resourcePath) throws Exception {

        InputStream inputStream =
                MigrationRunner.class
                        .getClassLoader()
                        .getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new RuntimeException(
                    "Migration file not found: " + resourcePath
            );
        }

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     inputStream,
                                     StandardCharsets.UTF_8))) {

            return reader.lines()
                    .collect(Collectors.joining("\n"));
        }
    }

    private static void executeSqlScript(
            Connection connection,
            String sqlScript) throws Exception {

        String cleanedSql = sqlScript
                .replaceAll("(?m)--.*$", "");

        String[] statements = cleanedSql.split(";");

        for (String sql : statements) {

            String trimmedSql = sql.trim();

            if (trimmedSql.isEmpty()) {
                continue;
            }

            try (PreparedStatement preparedStatement =
                         connection.prepareStatement(trimmedSql)) {

                preparedStatement.execute();
            }
        }
    }

    private static boolean isMigrationApplied(
            Connection connection,
            String version) throws Exception {

        String sql =
                "SELECT version FROM schema_version WHERE version = ?";

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(sql)) {

            preparedStatement.setString(1, version);

            try (ResultSet resultSet =
                         preparedStatement.executeQuery()) {

                return resultSet.next();
            }
        }
    }
}