package com.odms.tracking.controller;

import com.odms.tracking.dto.response.OrderResponse;
import com.odms.tracking.dto.response.Response;
import com.odms.tracking.service.ITrackingService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tracking")
@RequiredArgsConstructor
@Validated
public class TrackingController {
    private final ITrackingService trackingService;

    @GetMapping("/order/details")
    // This endpoint is for authenticated users to get order details
    // Authorization is handled by the service layer
    public ResponseEntity<Response<OrderResponse>> getOrderDetails(@RequestParam @NotBlank String orderCode) {
        OrderResponse order = trackingService.getOrderDetails(orderCode, null);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<OrderResponse>builder()
                        .data(order)
                .build());
    }

    @GetMapping("/public/order/details")
    public ResponseEntity<Response<OrderResponse>> getPublicOrderDetails(@RequestParam @NotBlank String orderCode,
                                                                         @RequestParam @NotBlank String phone) {
        OrderResponse order = trackingService.getOrderDetails(orderCode, phone);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<OrderResponse>builder()
                .data(order)
                .build());
    }


}
