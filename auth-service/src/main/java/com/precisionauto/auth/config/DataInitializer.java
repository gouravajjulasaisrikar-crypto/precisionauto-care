package com.precisionauto.auth.config;

import com.precisionauto.auth.entity.Role;
import com.precisionauto.auth.entity.User;
import com.precisionauto.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                // Seed Admin
                userRepository.save(new User(
                        "admin",
                        passwordEncoder.encode("admin123"),
                        "admin@precisionauto.com",
                        "System Administrator",
                        Role.ROLE_ADMIN
                ));

                // Seed Technician
                userRepository.save(new User(
                        "technician",
                        passwordEncoder.encode("tech123"),
                        "technician@precisionauto.com",
                        "Alex Mercer (Master Tech)",
                        Role.ROLE_TECHNICIAN
                ));

                // Seed Customer
                userRepository.save(new User(
                        "customer",
                        passwordEncoder.encode("cust123"),
                        "customer@precisionauto.com",
                        "John Miller (Fleet Manager)",
                        Role.ROLE_CUSTOMER
                ));
            }
        };
    }
}
