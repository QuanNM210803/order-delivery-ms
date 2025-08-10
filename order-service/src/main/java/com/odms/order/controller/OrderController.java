package com.odms.order.controller;

import com.odms.order.constant.Message;
import com.odms.order.dto.request.*;
import com.odms.order.dto.response.*;
import com.odms.order.service.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nmquan.commonlib.annotation.InternalRequest;
import nmquan.commonlib.dto.response.FilterResponse;
import nmquan.commonlib.dto.response.IDResponse;
import nmquan.commonlib.dto.response.Response;
import nmquan.commonlib.utils.LocalizationUtils;
import nmquan.commonlib.utils.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Response<IDResponse<String>>> createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        IDResponse<String> response = orderService.createOrder(orderRequest);
        return ResponseUtils.success(response, localizationUtils.getLocalizedMessage(Message.ORDER_CREATED));
    }

    @GetMapping("/internal/check-customer-id/{customerId}/{orderCode}")
    @InternalRequest
    public ResponseEntity<Response<Boolean>> checkCustomerId(@PathVariable Long customerId, @PathVariable String orderCode) {
        boolean exists = orderService.checkCustomerId(customerId, orderCode);
        return ResponseUtils.success(exists);
    }

    @GetMapping("/internal/order/{orderCode}")
    @InternalRequest
    public ResponseEntity<Response<OrderResponse>> getOrderByOrderCode(@PathVariable String orderCode) {
        OrderResponse response = orderService.getOrderByOrderCode(orderCode);
        return ResponseUtils.success(response);
    }

    @GetMapping("/filter/customer")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Response<FilterResponse<OrderFilterResponse>>> filterByCustomer(@Valid @ModelAttribute FilterOrderCustomer request) {
        FilterResponse<OrderFilterResponse> response = orderService.filterByCustomer(request);
        return ResponseUtils.success(response);
    }

    @GetMapping("/filter/delivery_staff")
    @PreAuthorize("hasAuthority('DELIVERY_STAFF')")
    public ResponseEntity<Response<FilterResponse<OrderFilterResponse>>> filterByDelivery(@Valid @ModelAttribute FilterOrderDelivery request) {
        FilterResponse<OrderFilterResponse> response = orderService.filterByDelivery(request);
        return ResponseUtils.success(response);
    }

    @GetMapping("/filter/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<FilterResponse<OrderFilterResponse>>> filterByAdmin(@Valid @ModelAttribute FilterOrderAdmin request) {
        FilterResponse<OrderFilterResponse> response = orderService.filterByAdmin(request);
        return ResponseUtils.success(response);
    }
}
