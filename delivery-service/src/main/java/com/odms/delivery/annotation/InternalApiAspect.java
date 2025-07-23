package com.odms.delivery.annotation;

import com.odms.delivery.exception.AppException;
import com.odms.delivery.exception.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class InternalApiAspect {
    @Value("${jwt.x-internal-token}")
    private String X_INTERNAL_TOKEN;

    @Around("@annotation(com.odms.delivery.annotation.InternalApi)")
    public Object handleInternalApi(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String xInternalToken = attributes.getRequest().getHeader("X-Internal-Token");
        if (xInternalToken == null || !xInternalToken.equals(X_INTERNAL_TOKEN)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return joinPoint.proceed();
    }
}
