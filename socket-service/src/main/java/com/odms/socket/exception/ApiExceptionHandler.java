package com.odms.socket.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.odms.socket.dto.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {
    @ExceptionHandler({AppException.class})
    protected ResponseEntity<Response<Object>> handleAppException(AppException appException) {
        log.error("AppException: {}", appException.getMessage(), appException);
        ErrorCode errorCode = appException.getErrorCode();
        Response<Object> responseError = Response.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(responseError);
    }

    // validate error
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Response<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validate error: {}", ex.getMessage(), ex);
        String message=ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        //handle PARAM parse error (for param)
        String[] errorCodes = ex.getBindingResult().getAllErrors().get(0).getCodes();
        if (Arrays.asList(errorCodes).contains("typeMismatch")) {
            ErrorCode errorCode=ErrorCode.INVALID_FORMAT;
            message= errorCode.getMessage();
        }
        Response<Object> responseError=Response.builder()
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    // JSON parse error (for request body)
    @ExceptionHandler({InvalidFormatException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response<Object>> handleInvalidFormatException(Exception ex) {
        log.error("JSON parse error: {}", ex.getMessage(), ex);
        ErrorCode errorCode=ErrorCode.INVALID_FORMAT;
        Response<Object> responseError=Response.builder()
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(responseError);
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Response<Object>> handleException(Exception ex) {
        log.error("Exception: {}", ex.getMessage(), ex);
        ErrorCode errorCode=ErrorCode.ERROR;
        Response<Object> responseError=Response.builder()
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(responseError);
    }

}
