package tn.esprit.esprit_market.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            User admin = User.builder()
                    .name("Super Admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("Admin123!"))
                    .role(Role.ADMIN)
                    .dateOfBirth(new java.util.Date(946684800000L)) // 2000-01-01
                    .isActive(true)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ ADMIN account created: admin@gmail.com / Admin123!");
        } else {
            System.out.println("ℹ️ ADMIN account already exists.");
        }
    }
}
