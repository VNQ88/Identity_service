package com.devteria.identity_service.configuration;

import java.util.HashSet;

import com.devteria.identity_service.exception.AppException;
import com.devteria.identity_service.exception.ErrorCode;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.devteria.identity_service.constant.PredefinedRole;
import com.devteria.identity_service.entity.Role;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.repository.RoleRepository;
import com.devteria.identity_service.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        log.info("Init application .....");
        return args -> {
            if (Boolean.FALSE.equals(userRepository.existsByUsername("admin"))) {
                HashSet<Role> roles = new HashSet<>();
                roleRepository.findById(PredefinedRole.ADMIN_ROLE).ifPresent(roles::add);

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("admin user has been created with default password: admin123, please change it");
            }
        };
    }


}
