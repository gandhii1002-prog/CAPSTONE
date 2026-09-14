package com.gandhimart.controller;

import com.gandhimart.util.DatabaseConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/api/v1/health")
public class HealthServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (
                Connection connection =
                        DatabaseConfig.getDataSource().getConnection()
        ) {

            boolean databaseAvailable =
                    connection.isValid(2);

            if (databaseAvailable) {

                response.setStatus(
                        HttpServletResponse.SC_OK
                );

                response.getWriter().write(
                        "{\"status\":\"ok\",\"db\":\"up\"}"
                );

            } else {

                response.setStatus(
                        HttpServletResponse.SC_SERVICE_UNAVAILABLE
                );

                response.getWriter().write(
                        "{\"status\":\"error\",\"db\":\"down\"}"
                );
            }

        } catch (Exception e) {

            response.setStatus(
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE
            );

            response.getWriter().write(
                    "{\"status\":\"error\",\"db\":\"down\"}"
            );
        }
    }
}