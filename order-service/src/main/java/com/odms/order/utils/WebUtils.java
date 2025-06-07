package com.odms.order.utils;

import com.odms.order.dto.UserInfo;
import com.odms.order.exception.AppException;
import com.odms.order.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

    public static String getCurrentPhone() {
        return getCurrentUser().getPhone();
    }
}
