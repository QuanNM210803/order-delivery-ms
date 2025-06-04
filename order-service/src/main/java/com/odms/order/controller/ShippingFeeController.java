package com.odms.order.controller;

import com.odms.order.dto.response.Response;
import com.odms.order.dto.response.ShippingMatrixResponse;
import com.odms.order.service.IShippingFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shipping-fee")
public class ShippingFeeController {
    private final IShippingFeeService shippingFeeService;

    @GetMapping("/matrix")
    public ResponseEntity<Response<ShippingMatrixResponse>> getMatrix() {
        ShippingMatrixResponse response = shippingFeeService.getShippingMatrix();
        return ResponseEntity.status(HttpStatus.OK).body(
            Response.<ShippingMatrixResponse>builder()
                .data(response)
                .build()
        );
    }
}
