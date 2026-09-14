package com.gandhimart.util;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Collectors;

public class MigrationRunner {

    private MigrationRunner() {
    }

    /*
     * This method keeps compatibility with AppContextListener.
     */
    public static void runMigrations() {

        runMigrations(DatabaseConfig.getDataSource());
    }

    /*
     * Executes database migrations.
     */
    public static void runMigrations(DataSource dataSource) {

        try (Connection connection = dataSource.getConnection()) {

            // Create migration history table
            try (Statement statement = connection.createStatement()) {

                statement.execute("""
                    CREATE TABLE IF NOT EXISTS schema_version (
                        version VARCHAR(50) PRIMARY KEY,
                        description VARCHAR(255),
                        installed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
            }

            // Check whether V1 has already been executed
            if (isMigrationApplied(connection, "V1")) {

                System.out.println(
                        "Migration V1 already applied. Skipping."
                );

                return;
            }

            System.out.println(
                    "Running database migration V1..."
            );

            // Load SQL migration file
            InputStream inputStream =
                    MigrationRunner.class
                            .getClassLoader()
                            .getResourceAsStream(
                                    "db/migrations/V1__init_schema.sql"
                            );

            if (inputStream == null) {

                throw new RuntimeException(
                        "Migration file not found: " +
                        "db/migrations/V1__init_schema.sql"
                );
            }

            String sql;

            try (BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(
                                         inputStream,
                                         StandardCharsets.UTF_8))) {

                sql = reader.lines()
                        .collect(Collectors.joining("\n"));
            }

            // Execute the migration SQL
            try (Statement statement =
                         connection.createStatement()) {

                statement.execute(sql);
            }

            // Record successful migration
            try (PreparedStatement preparedStatement =
                         connection.prepareStatement(
                                 """
                                 INSERT INTO schema_version
                                 (version, description)
                                 VALUES (?, ?)
                                 """
                         )) {

                preparedStatement.setString(1, "V1");
                preparedStatement.setString(
                        2,
                        "Initial GandhiMart database schema"
                );

                preparedStatement.executeUpdate();
            }

            System.out.println(
                    "Migration V1 completed successfully."
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Database migration failed.",
                    e
            );
        }
    }

    /*
     * Checks whether a migration has already been executed.
     */
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