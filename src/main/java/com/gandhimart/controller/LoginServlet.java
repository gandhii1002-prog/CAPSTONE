package com.gandhimart.controller;

import com.gandhimart.dao.UserDAOImpl;
import com.gandhimart.model.User;
import com.gandhimart.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/v1/auth/login")
public class LoginServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService(new UserDAOImpl());
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            User user = userService.login(email, password);

            HttpSession session = request.getSession(true);

            // Regenerate the session ID after successful authentication.
            request.changeSessionId();

            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole());

            response.setStatus(HttpServletResponse.SC_OK);

            response.getWriter().write(
                    "{"
                    + "\"message\":\"Login successful\","
                    + "\"userId\":" + user.getId() + ","
                    + "\"name\":\"" + escapeJson(user.getName()) + "\","
                    + "\"role\":\"" + escapeJson(user.getRole()) + "\""
                    + "}"
            );

        } catch (IllegalArgumentException e) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().write(
                    "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().write(
                    "{\"error\":\"Login failed\"}"
            );
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}