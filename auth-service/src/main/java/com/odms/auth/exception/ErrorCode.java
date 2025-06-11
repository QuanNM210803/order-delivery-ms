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
    USER_NOT_VERIFIED(1004, "Tài khoản chưa được xác thực qua Email", HttpStatus.UNAUTHORIZED),
    USER_ALREADY_VERIFIED(1005, "Tài khoản đã được xác thực qua Email", HttpStatus.BAD_REQUEST),
    UPDATE_STATUS_FINDING_ORDER_FAILED(1006, "Cập nhật trạng thái tìm đơn thất bại", HttpStatus.OK),
    EMAIL_EXISTS(1007, "Email đã tồn tại", HttpStatus.BAD_REQUEST),

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
