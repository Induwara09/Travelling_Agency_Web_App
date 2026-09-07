package com.example.travel.config;

import com.example.travel.entity.User;
import com.example.travel.enums.Role;
import com.example.travel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminName;
    private final String adminEmail;
    private final String adminPassword;

    public AdminDataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.name:}") String adminName,
            @Value("${app.admin.email:}") String adminEmail,
            @Value("${app.admin.password:}") String adminPassword
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (!StringUtils.hasText(adminEmail) || !StringUtils.hasText(adminPassword)) {
            return;
        }

        String normalizedEmail = adminEmail.trim().toLowerCase();
        User admin = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseGet(User::new);

        if (admin.getId() == null) {
            admin.setEmail(normalizedEmail);
        }
        if (StringUtils.hasText(adminName)) {
            admin.setName(adminName.trim());
        } else if (!StringUtils.hasText(admin.getName())) {
            admin.setName("Administrator");
        }

        // ADMIN_* environment variables are the source of truth for the
        // bootstrap account. This also repairs an existing CUSTOMER row or a
        // password that was inserted directly into MySQL without BCrypt.
        admin.setRole(Role.ADMIN);
        if (!passwordEncoder.matches(adminPassword, admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(adminPassword));
        }
        userRepository.save(admin);
    }
}
