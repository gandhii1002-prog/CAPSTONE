package com.gandhimart.testutil;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class H2TestDatabase {

        private static final AtomicLong DATABASE_ID =
                        new AtomicLong();

    public static DataSource create() throws Exception {

        JdbcDataSource dataSource =
                new JdbcDataSource();

        dataSource.setURL(
                "jdbc:h2:mem:test-"
                        + DATABASE_ID.incrementAndGet()
                        + ";DB_CLOSE_DELAY=-1"
        );

        dataSource.setUser("sa");
        dataSource.setPassword("");

        try (Connection connection =
                     dataSource.getConnection()) {

            runMigration(
                    connection,
                    "db/migrations/V1__init_schema.sql"
            );

            runMigration(
                    connection,
                    "db/migrations/V2__add_product_active_status.sql"
            );

            runMigration(
                    connection,
                    "db/migrations/V3__add_reviews_table.sql"
            );

            runMigration(
                    connection,
                    "db/migrations/V4__harden_browse_indexes.sql"
            );
        }

        return dataSource;
    }

    private static void runMigration(
            Connection connection,
            String resourcePath) throws Exception {

        InputStream inputStream =
                H2TestDatabase.class
                        .getClassLoader()
                        .getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IllegalStateException(
                    "Test migration not found: "
                            + resourcePath
            );
        }

        String sql;

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     inputStream,
                                     StandardCharsets.UTF_8
                             )
                     )) {

            sql = reader.lines()
                    .collect(Collectors.joining("\n"));
        }

        String[] statements =
                sql.split(";");

        for (String statementSql :
                statements) {

            String statementText =
                    statementSql.trim();

            if (statementText.isEmpty()) {
                continue;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 statementText
                         )) {

                statement.execute();
            }
        }
    }
}