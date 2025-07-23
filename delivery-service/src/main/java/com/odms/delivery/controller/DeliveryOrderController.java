package com.odms.delivery.controller;

import com.odms.delivery.annotation.InternalApi;
import com.odms.delivery.dto.request.UpdateDeliveryStatusRequest;
import com.odms.delivery.dto.response.Response;
import com.odms.delivery.dto.response.UpdateDeliveryStatusResponse;
import com.odms.delivery.dto.response.internal.DeliveryInfo;
import com.odms.delivery.service.IDeliveryOrderService;
import com.odms.delivery.utils.Message;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery-order")
@RequiredArgsConstructor
public class DeliveryOrderController {
    private final IDeliveryOrderService deliveryOrderService;

    @PatchMapping("/update-status")
    // This endpoint is for authenticated users to update the status of a delivery order
    // Authorization is handled by the service layer
    public ResponseEntity<Response<UpdateDeliveryStatusResponse>> updateDeliveryOrderStatus(@RequestBody @Valid UpdateDeliveryStatusRequest request) {
        UpdateDeliveryStatusResponse response = deliveryOrderService.updateDeliveryOrderStatus(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<UpdateDeliveryStatusResponse>builder()
                        .data(response)
                        .message(Message.UPDATE_DELIVERY_ORDER_STATUS_SUCCESS.getMessage())
                        .build()
        );
    }

    @GetMapping("/internal/status-history/{orderCode}")
    @InternalApi
    public ResponseEntity<Response<DeliveryInfo>> getDeliveryOrderStatusHistory(@PathVariable String orderCode) {
        DeliveryInfo response = deliveryOrderService.getDeliveryOrderStatusHistory(orderCode);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<DeliveryInfo>builder()
                        .data(response)
                        .build()
        );
    }
}
