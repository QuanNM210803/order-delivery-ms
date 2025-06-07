package com.odms.order.controller;

import com.odms.order.dto.request.OrderRequest;
import com.odms.order.dto.response.IDResponse;
import com.odms.order.dto.response.OrderResponse;
import com.odms.order.dto.response.Response;
import com.odms.order.exception.AppException;
import com.odms.order.exception.ErrorCode;
import com.odms.order.service.IOrderService;
import com.odms.order.utils.Message;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @Value("${jwt.x-internal-token}")
    private String X_INTERNAL_TOKEN;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Response<IDResponse<String>>> createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        IDResponse<String> response = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<IDResponse<String>>builder()
                        .data(response)
                        .message(Message.ORDER_CREATED.getMessage())
                .build());
    }

    @GetMapping("/internal/check-customer-id/{customerId}/{orderCode}")
    public ResponseEntity<Response<Boolean>> checkCustomerId(@PathVariable Integer customerId,
                                                            @PathVariable String orderCode,
                                                             HttpServletRequest request) {
        String x_internal_token = request.getHeader("X-Internal-Token");
        if (!X_INTERNAL_TOKEN.equals(x_internal_token)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        boolean exists = orderService.checkCustomerId(customerId, orderCode);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<Boolean>builder()
                .data(exists)
                .build());
    }

    @GetMapping("/internal/order/{orderCode}")
    public ResponseEntity<Response<OrderResponse>> checkCustomerId(@PathVariable String orderCode,
                                                                   HttpServletRequest request) {
        String x_internal_token = request.getHeader("X-Internal-Token");
        if (!X_INTERNAL_TOKEN.equals(x_internal_token)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        OrderResponse response = orderService.getOrderByOrderCode(orderCode);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<OrderResponse>builder()
                .data(response)
                .build());
    }
}
