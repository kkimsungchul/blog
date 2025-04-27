package com.sungchul.blog.config;

import com.sungchul.blog.entity.User;
import com.sungchul.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if it doesn't exist
        if (!userService.existsByUsername("admin")) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword("admin123"); // Will be encoded by UserService
            adminUser.setEmail("admin@example.com");
            adminUser.setName("Administrator");
            adminUser.setAdmin(true);
            
            userService.createUser(adminUser);
            System.out.println("Admin user created successfully");
        }
        
        // Create regular user if it doesn't exist
        if (!userService.existsByUsername("user")) {
            User regularUser = new User();
            regularUser.setUsername("user");
            regularUser.setPassword("user123"); // Will be encoded by UserService
            regularUser.setEmail("user@example.com");
            regularUser.setName("Regular User");
            regularUser.setAdmin(false);
            
            userService.createUser(regularUser);
            System.out.println("Regular user created successfully");
        }
    }
}