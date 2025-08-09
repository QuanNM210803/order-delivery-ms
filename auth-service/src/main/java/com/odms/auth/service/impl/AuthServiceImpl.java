package com.odms.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odms.auth.config.security.JwtTokenUtils;
import com.odms.auth.dto.RoleName;
import com.odms.auth.dto.TypeMail;
import com.odms.auth.dto.event.NotificationEvent;
import com.odms.auth.dto.request.LoginRequest;
import com.odms.auth.dto.request.RegisterRequest;
import com.odms.auth.dto.request.VerifyRequest;
import com.odms.auth.dto.response.LoginResponse;
import com.odms.auth.dto.response.VerifyResponse;
import com.odms.auth.entity.DeliveryStaff;
import com.odms.auth.entity.Role;
import com.odms.auth.entity.User;
import com.odms.auth.entity.UserRole;
import com.odms.auth.enums.AuthErrorCode;
import com.odms.auth.repository.DeliveryStaffRepository;
import com.odms.auth.repository.RoleRepository;
import com.odms.auth.repository.UserRepository;
import com.odms.auth.repository.UserRoleRepository;
import com.odms.auth.service.IAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nmquan.commonlib.dto.response.IDResponse;
import nmquan.commonlib.exception.AppException;
import nmquan.commonlib.exception.CommonErrorCode;
import nmquan.commonlib.model.JwtUser;
import nmquan.commonlib.utils.JwtUtils;
import nmquan.commonlib.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DeliveryStaffRepository deliveryStaffRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${frontend.url}")
    private String FRONTEND_URL;

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.verify-email.secret-key}")
    private String SECRET_KEY_VERIFY_EMAIL;

    @Override
    public LoginResponse loginAccount(LoginRequest loginRequest) {
        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername(), false);
        if (user.isEmpty()) {
            throw new AppException(AuthErrorCode.LOGIN_FAILED);
        }
        if(!user.get().getIsVerified()) {
            throw new AppException(AuthErrorCode.USER_NOT_VERIFIED);
        }
        try{
            Authentication authentication= authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        } catch (BadCredentialsException ex) {
            // wrong password
            throw new AppException(AuthErrorCode.LOGIN_FAILED);
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
            JwtUtils.validate(token, SECRET_KEY);
        } catch (Exception e) {
            isValid = false;
        }

        return VerifyResponse.builder()
                .isValid(isValid)
                .build();
    }

    @Override
    @Transactional
    public IDResponse<Long> registerAccount(RegisterRequest request, String roleName) throws JsonProcessingException {
        userRepository.findByUsername(request.getUsername(), false).ifPresent(user -> {
            throw new AppException(AuthErrorCode.USERNAME_EXISTS);
        });
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new AppException(AuthErrorCode.EMAIL_EXISTS);
        });

        Role role = roleRepository.findByName(roleName, false).orElseThrow(
                () -> new AppException(CommonErrorCode.ERROR)
        );
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .isVerified(false)
                .build();
        userRepository.save(user);
        userRoleRepository.save(UserRole.builder()
                        .user(user)
                        .role(role)
                    .build());

        String token = this.jwtTokenUtils.generateTokenVerifyEmail(user);
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .typeMail(TypeMail.REGISTER)
                .recipient(user.getEmail())
                .content("Please click the link to verify your email: " +
                        FRONTEND_URL + "/auth/verify-email/" + token)
                .build();
        kafkaTemplate.send("notification-topic", ObjectMapperUtils.convertToJson(notificationEvent));

        return IDResponse.<Long>builder()
                .id(user.getId())
                .build();
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        JwtUser jwtUser = JwtUtils.validate(token, SECRET_KEY_VERIFY_EMAIL);
        User user = userRepository.findByUsername(jwtUser.getUsername(), false)
                .orElseThrow(() -> new AppException(CommonErrorCode.UNAUTHENTICATED));

        if (user.getIsVerified()) {
            throw new AppException(AuthErrorCode.USER_ALREADY_VERIFIED);
        }

        user.setIsVerified(true);
        userRepository.save(user);
        Role role = roleRepository.findByName(RoleName.DELIVERY_STAFF.name(), false)
                .orElseThrow(() -> new AppException(CommonErrorCode.ERROR));
        UserRole userRole = userRoleRepository.findByUserIdAndRoleId(user.getId(), role.getId(), false)
                .orElseThrow(() -> new AppException(CommonErrorCode.ERROR));
        if(userRole != null) {
            DeliveryStaff deliveryStaff = DeliveryStaff.builder()
                    .user(user)
                    .findingOrder(false)
                    .build();
            deliveryStaffRepository.save(deliveryStaff);
        }

    }
}
