package com.odms.tracking.utils;

import com.odms.tracking.dto.UserInfo;
import com.odms.tracking.exception.AppException;
import com.odms.tracking.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;

public class WebUtils {

    public static UserInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return (UserInfo) authentication.getPrincipal();
    }

    public static Integer getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    public static String getCurrentFullName() {
        return getCurrentUser().getFullName();
    }

    public static String getCurrentEmail() {
        return getCurrentUser().getEmail();
    }

    public static List<String> getRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .toList();
    }
}
