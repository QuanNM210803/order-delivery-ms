package com.odms.auth.controller;

import com.odms.auth.annotation.InternalApi;
import com.odms.auth.dto.response.DeliveryStaffResponse;
import com.odms.auth.dto.response.IDResponse;
import com.odms.auth.dto.response.Response;
import com.odms.auth.service.IDeliveryStaffService;
import com.odms.auth.utils.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/delivery-staff")
@RequiredArgsConstructor
public class DeliveryStaffController {
    private final IDeliveryStaffService deliveryStaffService;

    @GetMapping("/my-status-finding-order")
    @PreAuthorize("hasAuthority('DELIVERY_STAFF')")
    public ResponseEntity<Response<Boolean>> getMyStatusFindingOrder() {
        Boolean response = deliveryStaffService.getMyStatusFindingOrder();
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<Boolean>builder()
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/update/status-finding-order")
    @PreAuthorize("hasAuthority('DELIVERY_STAFF')")
    public ResponseEntity<Response<IDResponse<Integer>>> updateStatusFindingOrder() {
        IDResponse<Integer> response = deliveryStaffService.updateStatusFindingOrder(null);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<IDResponse<Integer>>builder()
                        .data(response)
                        .message(Message.UPDATE_STATUS_FINDING_ORDER_SUCCESS.getMessage())
                        .build()
        );
    }

    @GetMapping("/internal/update/status-finding-order/{userId}")
    @InternalApi
    public ResponseEntity<Response<IDResponse<Integer>>> internalUpdateStatusFindingOrder(@PathVariable Integer userId) {
        IDResponse<Integer> response = deliveryStaffService.updateStatusFindingOrder(userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<IDResponse<Integer>>builder()
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/find-delivery-staff/{status}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<List<DeliveryStaffResponse>>> findDeliveryStaff(@PathVariable Boolean status) {
        List<DeliveryStaffResponse> deliveryStaffs = deliveryStaffService.findDeliveryStaff(status);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<List<DeliveryStaffResponse>>builder()
                        .data(deliveryStaffs)
                        .build()
        );
    }

}
