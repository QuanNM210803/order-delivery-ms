package com.odms.order.controller;

import com.odms.order.annotation.InternalApi;
import com.odms.order.dto.request.*;
import com.odms.order.dto.response.*;
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

    @GetMapping("/internal/check-customer-id/{customerId}/{orderCode}")
    @InternalApi
    public ResponseEntity<Response<Boolean>> checkCustomerId(@PathVariable Integer customerId, @PathVariable String orderCode) {
        boolean exists = orderService.checkCustomerId(customerId, orderCode);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<Boolean>builder()
                .data(exists)
                .build());
    }

    @GetMapping("/internal/order/{orderCode}")
    @InternalApi
    public ResponseEntity<Response<OrderResponse>> getOrderByOrderCode(@PathVariable String orderCode) {
        OrderResponse response = orderService.getOrderByOrderCode(orderCode);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<OrderResponse>builder()
                .data(response)
                .build());
    }

    @GetMapping("/filter/customer")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Response<FilterResponse<OrderFilterResponse>>> filterByCustomer(@Valid @ModelAttribute FilterOrderCustomer request) {
        FilterResponse<OrderFilterResponse> response = orderService.filterByCustomer(request);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<FilterResponse<OrderFilterResponse>>builder()
                .data(response)
                .build());
    }

    @GetMapping("/filter/delivery_staff")
    @PreAuthorize("hasAuthority('DELIVERY_STAFF')")
    public ResponseEntity<Response<FilterResponse<OrderFilterResponse>>> filterByDelivery(@Valid @ModelAttribute FilterOrderDelivery request) {
        FilterResponse<OrderFilterResponse> response = orderService.filterByDelivery(request);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<FilterResponse<OrderFilterResponse>>builder()
                .data(response)
                .build());
    }

    @GetMapping("/filter/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<FilterResponse<OrderFilterResponse>>> filterByAdmin(@Valid @ModelAttribute FilterOrderAdmin request) {
        FilterResponse<OrderFilterResponse> response = orderService.filterByAdmin(request);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<FilterResponse<OrderFilterResponse>>builder()
                .data(response)
                .build());
    }
}
