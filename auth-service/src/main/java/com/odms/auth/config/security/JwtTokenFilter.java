package com.odms.auth.config.security;

import com.odms.auth.entity.User;
import com.odms.auth.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import quannm.jwtauthlib.entity.JwtUser;
import quannm.jwtauthlib.jwt.JwtValidator;
import quannm.jwtauthlib.util.JwtUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter{

    private final UserRepository userRepository;

    @Value("${jwt.secretKey}")
    private String SECRET_KEY;

    private static final Logger logger= LoggerFactory.getLogger(JwtTokenFilter.class);
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ExpiredJwtException, IOException, ServletException {

        try{
            String jwt = JwtUtils.getToken(request);
            if(jwt != null){
                JwtUser jwtUser = JwtValidator.validate(jwt, SECRET_KEY);

                Optional<User> user = userRepository.findById(jwtUser.getUserId());
                List<GrantedAuthority> authorities = jwtUser.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                var authentication = new UsernamePasswordAuthenticationToken(user.get(), null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e){
            logger.error("Authenticated: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
