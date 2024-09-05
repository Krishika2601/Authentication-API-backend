package com.authentication.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.authentication.entity.Admin;
import com.authentication.repository.AdminRepository;


@Component
public class AdminInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminInitializer(AdminRepository adminRepository, BCryptPasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if default admin already exists
        Admin existingAdmin = adminRepository.findByUsername("admin@gmail.com");
        if (existingAdmin == null) {
            // Create default admin
            Admin admin = new Admin();
            admin.setUsername("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole("ADMIN");

            // Save the admin
            adminRepository.save(admin);
            System.out.println("Default admin created successfully.");
        } else {
            System.out.println("Default admin already exists.");
        }
    }
}
