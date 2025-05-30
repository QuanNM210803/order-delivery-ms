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

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        Collection<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();
        claims.put("roles", roles);
        claims.put("user_id", user.getUserId());
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

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Integer extractUserId(String token) {
        return (Integer) extractAllClaims(token).get("user_id");
    }

    public Collection<String> extractAuthorities(String token) {
        return (Collection<String>) extractAllClaims(token).get("roles");
    }
}

