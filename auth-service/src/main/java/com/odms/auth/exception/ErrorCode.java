package com.odms.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNAUTHENTICATED(401, "Lỗi xác thực", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(403, "Quyền truy cập bị từ chối", HttpStatus.FORBIDDEN),
    ERROR(500, "Đã xảy ra lỗi, vui lòng thử lại sau!", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FORMAT(101, "Sai định dạng dữ liệu đầu vào", HttpStatus.BAD_REQUEST),

    USERNAME_NOT_EXISTS(1001, "Tên đăng nhập không tồn tại", HttpStatus.UNAUTHORIZED),
    USERNAME_EXISTS(1002, "Tên đăng nhập đã tồn tại", HttpStatus.BAD_REQUEST),
    LOGIN_FAILED(1003, "Sai tên đăng nhập hoặc mật khẩu", HttpStatus.UNAUTHORIZED),

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
