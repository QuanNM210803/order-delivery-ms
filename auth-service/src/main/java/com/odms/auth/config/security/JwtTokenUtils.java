package com.odms.auth.config.security;

import com.odms.auth.entity.Role;
import com.odms.auth.entity.User;
import com.odms.auth.repository.RoleRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import nmquan.commonlib.exception.AppException;
import nmquan.commonlib.exception.CommonErrorCode;
import nmquan.commonlib.utils.JwtUtils;
import nmquan.commonlib.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    @Value("${jwt.expiration}")
    private int EXPIRATION;

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.verify-email.expiration}")
    private int EXPIRATION_VERIFY_EMAIL;

    @Value("${jwt.verify-email.secret-key}")
    private String SECRET_KEY_VERIFY_EMAIL;

    private final RoleRepository roleRepository;
    // functions to generate and verify JWT tokens
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = roleRepository.findAllByUserId(user.getId(), false).stream()
                .map(Role::getName)
                .toList();
        claims.put("roles", roles);

        user.setPassword(null);
        claims.put("user", ObjectMapperUtils.convertToJson(user));
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getUsername())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000L))
                    .signWith(JwtUtils.getSignInKey(this.SECRET_KEY), SignatureAlgorithm.HS256)
                    .compact();
        }
        catch (Exception e) {
            throw new AppException(CommonErrorCode.ERROR);
        }
    }

    // functions to generate and verify JWT tokens for email verification
    public String generateTokenVerifyEmail(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getUsername())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_VERIFY_EMAIL * 1000L))
                    .signWith(JwtUtils.getSignInKey(this.SECRET_KEY_VERIFY_EMAIL), SignatureAlgorithm.HS256)
                    .compact();
        }
        catch (Exception e) {
            throw new AppException(CommonErrorCode.ERROR);
        }
    }
}

