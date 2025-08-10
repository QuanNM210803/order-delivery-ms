package com.odms.delivery.enums;

import com.odms.delivery.constant.Message;
import lombok.Getter;
import nmquan.commonlib.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public enum DeliveryErrorCode implements ErrorCode {
    REASON_CANCEL_REQUIRED(2001, Message.ORDER_REASON_CANCEL_REQUIRED, HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(2002, Message.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND),
    ORDER_ALREADY_ASSIGNED(2003, Message.ORDER_ALREADY_ASSIGNED, HttpStatus.BAD_REQUEST),
    ORDER_ALREADY_CANCELLED(2004, Message.ORDER_ALREADY_CANCELLED, HttpStatus.BAD_REQUEST),
    ORDER_STATUS_NOT_ALLOW(2005, Message.ORDER_STATUS_NOT_ALLOW, HttpStatus.BAD_REQUEST)

    ;

    DeliveryErrorCode(Integer code, String message, HttpStatus statusCode) {
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
