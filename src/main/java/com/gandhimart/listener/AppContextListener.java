package com.gandhimart.listener;

import com.gandhimart.dao.UserDAOImpl;
import com.gandhimart.service.AdminSeedService;
import com.gandhimart.util.DatabaseConfig;
import com.gandhimart.util.MigrationRunner;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener
        implements ServletContextListener {

    @Override
    public void contextInitialized(
            ServletContextEvent event) {

        System.out.println(
                "========================================"
        );

        System.out.println(
                "GandhiMart application starting..."
        );

        System.out.println(
                "========================================"
        );

        try {

            DatabaseConfig.initialize();

            MigrationRunner.runMigrations(
                    DatabaseConfig.getDataSource()
            );

            AdminSeedService.seed(
                    new UserDAOImpl()
            );

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "Database migrations completed."
            );

            System.out.println(
                    "GandhiMart application started."
            );

            System.out.println(
                    "========================================"
            );

        } catch (Exception e) {

            System.err.println(
                    "========================================"
            );

            System.err.println(
                    "GandhiMart startup failed."
            );

            System.err.println(
                    "========================================"
            );

            e.printStackTrace();

            throw new RuntimeException(
                    "GandhiMart application startup failed.",
                    e
            );
        }
    }

    @Override
    public void contextDestroyed(
            ServletContextEvent event) {

        System.out.println(
                "GandhiMart application stopping..."
        );

        DatabaseConfig.close();

        System.out.println(
                "GandhiMart application stopped."
        );
    }
}