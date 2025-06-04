package com.odms.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odms.auth.config.JwtTokenUtils;
import com.odms.auth.dto.TypeMail;
import com.odms.auth.dto.event.NotificationEvent;
import com.odms.auth.dto.request.LoginRequest;
import com.odms.auth.dto.request.RegisterRequest;
import com.odms.auth.dto.request.VerifyRequest;
import com.odms.auth.dto.response.IDResponse;
import com.odms.auth.dto.response.LoginResponse;
import com.odms.auth.dto.response.VerifyResponse;
import com.odms.auth.entity.DeliveryStaff;
import com.odms.auth.entity.Role;
import com.odms.auth.entity.User;
import com.odms.auth.exception.AppException;
import com.odms.auth.exception.ErrorCode;
import com.odms.auth.repository.DeliveryStaffRepository;
import com.odms.auth.repository.RoleRepository;
import com.odms.auth.repository.UserRepository;
import com.odms.auth.service.IAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DeliveryStaffRepository deliveryStaffRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${frontend.url}")
    private String FRONTEND_URL;

    @Override
    public LoginResponse loginAccount(LoginRequest loginRequest) {
        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        if (user.isEmpty()) {
            throw new AppException(ErrorCode.LOGIN_FAILED);
        }
        if(!user.get().getIsVerified()) {
            throw new AppException(ErrorCode.USER_NOT_VERIFIED);
        }
        try{
            Authentication authentication= authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        } catch (BadCredentialsException ex) {
            // wrong password
            throw new AppException(ErrorCode.LOGIN_FAILED);
        }
        String jwt= jwtTokenUtils.generateToken(user.get());
        return LoginResponse.builder()
                .token(jwt)
                .build();
    }

    @Override
    public VerifyResponse verifyToken(VerifyRequest verifyRequest) {
        String token = verifyRequest.getToken();
        boolean isValid = true;

        try {
            jwtTokenUtils.verifyToken(token);
        } catch (Exception e) {
            isValid = false;
        }

        return VerifyResponse.builder()
                .isValid(isValid)
                .build();
    }

    @Override
    public IDResponse<Integer> registerAccount(RegisterRequest request, String roleName) throws JsonProcessingException {
        userRepository.findByUsername(request.getUsername()).ifPresent(user -> {
            throw new AppException(ErrorCode.USERNAME_EXISTS);
        });

        Role role = roleRepository.findByName(roleName).orElse(null);
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .isVerified(false)
                .roles(Collections.singleton(role))
                .build();
        userRepository.save(user);

        String token = this.jwtTokenUtils.generateTokenVerifyEmail(user);
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .typeMail(TypeMail.REGISTER)
                .recipient(user.getEmail())
                .content("Please click the link to verify your email: " +
                        FRONTEND_URL + "/auth/verify-email/" + token)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String notificationJson = objectMapper.writeValueAsString(notificationEvent);
        kafkaTemplate.send("notification-topic", notificationJson);

        return IDResponse.<Integer>builder()
                .id(user.getUserId())
                .build();
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        Integer userId = jwtTokenUtils.extractUserIdVerifyEmail(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (user.getIsVerified()) {
            throw new AppException(ErrorCode.USER_ALREADY_VERIFIED);
        }

        user.setIsVerified(true);
        userRepository.save(user);

        if(user.getRoles().stream().anyMatch(role -> role.getName().equals("DELIVERY_STAFF"))) {
            DeliveryStaff deliveryStaff = DeliveryStaff.builder()
                    .user(user)
                    .findingOrder(false)
                    .build();
            deliveryStaffRepository.save(deliveryStaff);
        }

    }
}
