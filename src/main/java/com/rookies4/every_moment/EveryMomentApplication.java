package com.rookies4.every_moment;

import com.rookies4.every_moment.user.User;
import com.rookies4.every_moment.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EveryMomentApplication {

    public static void main(String[] args) {
        SpringApplication.run(EveryMomentApplication.class, args);
    }

    @Bean
    CommandLineRunner seed(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (users.findByEmail("admin@example.com").isEmpty()) {
                users.save(User.builder()
                        .username("admin")
                        .email("admin@example.com")
                        .passwordHash(encoder.encode("AdminPassw0rd!"))
                        .role("ROLE_ADMIN")
                        .smoking(false)
                        .active(true)
                        .build());
            }
            if (users.findByEmail("demo@example.com").isEmpty()) {
                users.save(User.builder()
                        .username("demo")
                        .email("demo@example.com")
                        .passwordHash(encoder.encode("Passw0rd!"))
                        .role("ROLE_USER")
                        .smoking(false)
                        .active(true)
                        .build());
            }
        };
    }
}
