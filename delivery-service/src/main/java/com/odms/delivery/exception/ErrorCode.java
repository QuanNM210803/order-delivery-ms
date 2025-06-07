package com.odms.delivery.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNAUTHENTICATED(401, "Lỗi xác thực", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(403, "Quyền truy cập bị từ chối", HttpStatus.FORBIDDEN),
    ERROR(500, "Đã xảy ra lỗi, vui lòng thử lại sau!", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FORMAT(101, "Sai định dạng dữ liệu đầu vào", HttpStatus.BAD_REQUEST),

    REASON_CANCEL_REQUIRED(2001, "Yêu cầu lý do huỷ đơn hàng", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(2002, "Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND),
    ORDER_ALREADY_ASSIGNED(2003, "Đơn hàng đã được giao cho nhân viên giao hàng", HttpStatus.BAD_REQUEST),
    ORDER_ALREADY_CANCELLED(2004, "Đơn hàng đã bị huỷ", HttpStatus.BAD_REQUEST),
    ORDER_STATUS_NOT_ALLOW(2005, "Không thể cập nhật trạng thái này", HttpStatus.BAD_REQUEST)

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
