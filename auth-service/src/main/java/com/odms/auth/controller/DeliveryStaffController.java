package com.odms.auth.controller;

import com.odms.auth.constant.Message;
import com.odms.auth.dto.response.DeliveryStaffResponse;
import com.odms.auth.service.IDeliveryStaffService;
import lombok.RequiredArgsConstructor;
import nmquan.commonlib.annotation.InternalRequest;
import nmquan.commonlib.dto.response.IDResponse;
import nmquan.commonlib.dto.response.Response;
import nmquan.commonlib.utils.LocalizationUtils;
import nmquan.commonlib.utils.ResponseUtils;
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
    private final LocalizationUtils localizationUtils;

    @GetMapping("/my-status-finding-order")
    @PreAuthorize("hasAuthority('DELIVERY_STAFF')")
    public ResponseEntity<Response<Boolean>> getMyStatusFindingOrder() {
        Boolean response = deliveryStaffService.getMyStatusFindingOrder();
        return ResponseUtils.success(response);
    }

    @PatchMapping("/update/status-finding-order")
    @PreAuthorize("hasAuthority('DELIVERY_STAFF')")
    public ResponseEntity<Response<IDResponse<Long>>> updateStatusFindingOrder() {
        IDResponse<Long> response = deliveryStaffService.updateStatusFindingOrder(null);
        return ResponseUtils.success(response, localizationUtils.getLocalizedMessage(Message.UPDATE_STATUS_FINDING_ORDER_SUCCESS));
    }

    @GetMapping("/internal/update/status-finding-order/{userId}")
    @InternalRequest
    public ResponseEntity<Response<IDResponse<Long>>> internalUpdateStatusFindingOrder(@PathVariable Long userId) {
        IDResponse<Long> response = deliveryStaffService.updateStatusFindingOrder(userId);
        return ResponseUtils.success(response);
    }

    @GetMapping("/find-delivery-staff/{status}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<List<DeliveryStaffResponse>>> findDeliveryStaff(@PathVariable Boolean status) {
        List<DeliveryStaffResponse> deliveryStaffs = deliveryStaffService.findDeliveryStaff(status);
        return ResponseUtils.success(deliveryStaffs);
    }

}
