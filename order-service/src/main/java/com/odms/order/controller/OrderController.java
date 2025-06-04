package com.odms.order.controller;

import com.odms.order.dto.request.OrderRequest;
import com.odms.order.dto.response.IDResponse;
import com.odms.order.dto.response.Response;
import com.odms.order.service.IOrderService;
import com.odms.order.utils.Message;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Response<IDResponse<String>>> createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        IDResponse<String> response = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<IDResponse<String>>builder()
                        .data(response)
                        .message(Message.ORDER_CREATED.getMessage())
                .build());
    }
}
