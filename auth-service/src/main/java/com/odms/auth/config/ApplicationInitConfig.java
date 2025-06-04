package com.odms.auth.config;

import com.odms.auth.entity.DeliveryStaff;
import com.odms.auth.entity.Role;
import com.odms.auth.entity.User;
import com.odms.auth.repository.DeliveryStaffRepository;
import com.odms.auth.repository.RoleRepository;
import com.odms.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Collections;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    @Transactional
    ApplicationRunner applicationRunner(UserRepository userRepository,
                                        RoleRepository roleRepository,
                                        DeliveryStaffRepository deliveryStaffRepository) {
        log.info("Initializing application.....");
        return args -> {
            Role customerRole = roleRepository.findByName("CUSTOMER")
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .name("CUSTOMER")
                            .build()));
            Role deliveryStaffRole = roleRepository.findByName("DELIVERY_STAFF")
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .name("DELIVERY_STAFF")
                            .build()));
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .name("ADMIN")
                            .build()));

            if (userRepository.findByUsername("customer").isEmpty()) {
                User customer = User.builder()
                        .username("customer")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("Customer User")
                        .phone("0123456789")
                        .email("nnmhqn2003@gmail.com")
                        .address("Hungyen, Vietnam")
                        .isVerified(true)
                        .roles(Collections.singleton(customerRole))
                        .build();
                userRepository.save(customer);
            }

            if (userRepository.findByUsername("deliverystaff").isEmpty()) {
                User deliverystaff = User.builder()
                        .username("deliverystaff")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("DeliveryStaff User")
                        .phone("0123456789")
                        .email("huykeo2022@gmail.com")
                        .address("Hanoi, Vietnam")
                        .isVerified(true)
                        .roles(Collections.singleton(deliveryStaffRole))
                        .build();
                userRepository.save(deliverystaff);
                DeliveryStaff ds = DeliveryStaff.builder()
                        .user(deliverystaff)
                        .findingOrder(false)
                        .build();
                deliveryStaffRepository.save(ds);
            }

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("Admin User")
                        .phone("0123456789")
                        .email("nnmhqn@gmail.com")
                        .address("Hanoi, Vietnam")
                        .isVerified(true)
                        .roles(Collections.singleton(adminRole))
                        .build();
                userRepository.save(admin);
            }
            log.info("Application initialization completed .....");
        };
    }
}