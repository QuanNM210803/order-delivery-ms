package com.odms.delivery.controller;

import com.odms.delivery.dto.request.UpdateDeliveryStatusRequest;
import com.odms.delivery.dto.response.IDResponse;
import com.odms.delivery.dto.response.Response;
import com.odms.delivery.dto.response.UpdateDeliveryStatusResponse;
import com.odms.delivery.dto.response.internal.DeliveryInfo;
import com.odms.delivery.exception.AppException;
import com.odms.delivery.exception.ErrorCode;
import com.odms.delivery.service.IDeliveryOrderService;
import com.odms.delivery.utils.Message;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery-order")
@RequiredArgsConstructor
public class DeliveryOrderController {
    private final IDeliveryOrderService deliveryOrderService;

    @Value("${jwt.x-internal-token}")
    private String X_INTERNAL_TOKEN;

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
    public ResponseEntity<Response<DeliveryInfo>> getDeliveryOrderStatusHistory(@PathVariable String orderCode,
                                                                                HttpServletRequest request) {
        String x_internal_token = request.getHeader("X-Internal-Token");
        if (!X_INTERNAL_TOKEN.equals(x_internal_token)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        DeliveryInfo response = deliveryOrderService.getDeliveryOrderStatusHistory(orderCode);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<DeliveryInfo>builder()
                        .data(response)
                        .build()
        );
    }
}
