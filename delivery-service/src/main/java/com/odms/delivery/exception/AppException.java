package com.odms.delivery.exception;

import com.odms.delivery.dto.response.Response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {
    private ErrorCode errorCode;

    // for error from service other
    private Response<?> response;
    private Integer statusCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AppException(Response<Object> response, Integer statusCode) {
        super(response.getMessage());
        this.response = response;
        this.statusCode = statusCode;
    }

}
