package com.odms.tracking.config;

import com.odms.tracking.dto.UserInfo;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter{

    private final JwtTokenUtils jwtTokenUtil;

    private static final Logger logger= LoggerFactory.getLogger(JwtTokenFilter.class);
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ExpiredJwtException, IOException, ServletException {

        try{
            String jwt = parseJwt(request);
            if(jwt != null){
                Integer userId = this.jwtTokenUtil.extractUserId(jwt);
                String fullName = this.jwtTokenUtil.extractFullName(jwt);
                String email = this.jwtTokenUtil.extractEmail(jwt);
                UserInfo userInfo = UserInfo.builder()
                        .userId(userId)
                        .fullName(fullName)
                        .email(email)
                        .build();

                Collection<String> roles = this.jwtTokenUtil.extractAuthorities(jwt);
                List<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                var authentication = new UsernamePasswordAuthenticationToken(userInfo, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e){
            logger.error("Authenticated: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth=
                (request.getHeader("Authorization")==null || request.getHeader("Authorization").isEmpty())
                        ? request.getParameter("token") : request.getHeader("Authorization");
        if(!StringUtils.hasText(headerAuth)){
            return null;
        }
        if(headerAuth.startsWith("Bearer ")){
            return headerAuth.substring(7);
        }
        return headerAuth;
    }

}
