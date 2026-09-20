package com.gandhimart.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/api/v1/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request =
                (HttpServletRequest) servletRequest;

        HttpServletResponse response =
                (HttpServletResponse) servletResponse;

        String path =
                request.getRequestURI()
                        .substring(
                                request.getContextPath().length()
                        );

        if (isPublicEndpoint(request, path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session =
                request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json;charset=UTF-8"
            );

            response.setHeader(
                    "Cache-Control",
                    "no-store"
            );

            response.getWriter().write(
                    "{\"error\":\"Authentication required\"}"
            );

            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(
            HttpServletRequest request,
            String path) {

        if ("/api/v1/health".equals(path)) {
            return true;
        }

        if ("/api/v1/auth/login".equals(path)) {
            return true;
        }

        if ("/api/v1/auth/register".equals(path)) {
            return true;
        }

        if ("/api/v1/products".equals(path) &&
                "GET".equalsIgnoreCase(
                        request.getMethod()
                )) {

            return true;
        }

        if ("/api/v1/reviews".equals(path) &&
                "GET".equalsIgnoreCase(
                        request.getMethod()
                )) {

            return true;
        }

        return false;
    }
}