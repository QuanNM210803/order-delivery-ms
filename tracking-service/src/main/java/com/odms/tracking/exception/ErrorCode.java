package com.odms.tracking.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNAUTHENTICATED(401, "Lỗi xác thực", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(403, "Quyền truy cập bị từ chối", HttpStatus.FORBIDDEN),
    ERROR(500, "Đã xảy ra lỗi, vui lòng thử lại sau!", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FORMAT(101, "Sai định dạng dữ liệu đầu vào", HttpStatus.BAD_REQUEST),

    ;

    ErrorCode(Integer code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final Integer code;
    private final String message;
    private final HttpStatusCode statusCode;
}
