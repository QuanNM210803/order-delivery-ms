package com.odms.auth.config;

import com.odms.auth.entity.Role;
import com.odms.auth.entity.User;
import com.odms.auth.exception.AppException;
import com.odms.auth.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    @Value("${jwt.expiration}")
    private int EXPIRATION;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.verify-email.expiration}")
    private int EXPIRATION_VERIFY_EMAIL;

    @Value("${jwt.verify-email.secretKey}")
    private String SECRET_KEY_VERIFY_EMAIL;

    // functions to generate and verify JWT tokens
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        Collection<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();
        claims.put("roles", roles);
        claims.put("user_id", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("phone", user.getPhone());
        claims.put("full_name", user.getFullName());
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getUsername())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        }
        catch (Exception e) {
            throw new AppException(ErrorCode.ERROR);
        }
    }

    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void verifyToken(String token) {
        extractAllClaims(token);
    }

    public Integer extractUserId(String token) {
        return (Integer) extractAllClaims(token).get("user_id");
    }

    public Collection<String> extractAuthorities(String token) {
        return (Collection<String>) extractAllClaims(token).get("roles");
    }


    // functions to generate and verify JWT tokens for email verification
    public String generateTokenVerifyEmail(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getUserId());
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getUsername())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_VERIFY_EMAIL * 1000L))
                    .signWith(getSignInKeyVerifyEmail(), SignatureAlgorithm.HS256)
                    .compact();
        }
        catch (Exception e) {
            throw new AppException(ErrorCode.ERROR);
        }
    }

    private Key getSignInKeyVerifyEmail() {
        byte[] bytes = Decoders.BASE64.decode(SECRET_KEY_VERIFY_EMAIL);
        return Keys.hmacShaKeyFor(bytes);
    }

    private Claims extractAllClaimsVerifyEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKeyVerifyEmail())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Integer extractUserIdVerifyEmail(String token) {
        return (Integer) extractAllClaimsVerifyEmail(token).get("user_id");
    }

}

