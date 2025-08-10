package com.odms.delivery.controller;

import com.odms.delivery.dto.request.UpdateDeliveryStatusRequest;
import com.odms.delivery.dto.response.UpdateDeliveryStatusResponse;
import com.odms.delivery.dto.response.internal.DeliveryInfo;
import com.odms.delivery.service.IDeliveryOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nmquan.commonlib.annotation.InternalRequest;
import nmquan.commonlib.dto.response.Response;
import nmquan.commonlib.utils.ResponseUtils;
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
        return ResponseUtils.success(response);
    }

    @GetMapping("/internal/status-history/{orderCode}")
    @InternalRequest
    public ResponseEntity<Response<DeliveryInfo>> getDeliveryOrderStatusHistory(@PathVariable String orderCode) {
        DeliveryInfo response = deliveryOrderService.getDeliveryOrderStatusHistory(orderCode);
        return ResponseUtils.success(response);
    }
}
