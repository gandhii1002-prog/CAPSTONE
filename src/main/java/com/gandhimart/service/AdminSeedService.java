package com.gandhimart.service;

import com.gandhimart.dao.UserDAO;
import com.gandhimart.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class AdminSeedService {

    private static final String ADMIN_EMAIL =
            System.getenv()
                    .getOrDefault(
                            "GANDHIMART_ADMIN_EMAIL",
                            "admin@gandhimart.local"
                    );

    private static final String ADMIN_PASSWORD =
            System.getenv()
                    .getOrDefault(
                            "GANDHIMART_ADMIN_PASSWORD",
                            "Admin@12345"
                    );

    private AdminSeedService() {
    }

    public static void seed(UserDAO userDAO)
            throws SQLException {

        if (userDAO.findByEmail(ADMIN_EMAIL).isPresent()) {
            return;
        }

        User admin = new User();

        admin.setName("GandhiMart Admin");
        admin.setEmail(ADMIN_EMAIL);
        admin.setPasswordHash(
                BCrypt.hashpw(
                        ADMIN_PASSWORD,
                        BCrypt.gensalt()
                )
        );
        admin.setRole("ADMIN");

        userDAO.create(admin);

        System.out.println(
                "GandhiMart admin seed account created: "
                        + ADMIN_EMAIL
        );
    }
}