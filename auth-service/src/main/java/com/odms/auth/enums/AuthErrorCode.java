package com.odms.auth.enums;

import com.odms.auth.constant.Message;
import nmquan.commonlib.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum AuthErrorCode implements ErrorCode {

    USERNAME_NOT_EXISTS(1001, Message.USERNAME_NOT_EXISTS, HttpStatus.UNAUTHORIZED),
    USERNAME_EXISTS(1002, Message.USERNAME_EXISTS, HttpStatus.BAD_REQUEST),
    LOGIN_FAILED(1003, Message.LOGIN_FAILED, HttpStatus.UNAUTHORIZED),
    USER_NOT_VERIFIED(1004, Message.USER_NOT_VERIFIED, HttpStatus.UNAUTHORIZED),
    USER_ALREADY_VERIFIED(1005, Message.USER_ALREADY_VERIFIED, HttpStatus.BAD_REQUEST),
    UPDATE_STATUS_FINDING_ORDER_FAILED(1006, Message.UPDATE_STATUS_FINDING_ORDER_FAILED, HttpStatus.OK),
    EMAIL_EXISTS(1007, Message.EMAIL_EXISTS, HttpStatus.BAD_REQUEST),

    ;

    AuthErrorCode(Integer code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final Integer code;
    private final String message;
    private final HttpStatus statusCode;

    @Override
    public HttpStatus getStatusCode() {
        return statusCode;
    }
    @Override
    public Integer getCode() {
        return code;
    }
    @Override
    public String getMessage() {
        return message;
    }
}
