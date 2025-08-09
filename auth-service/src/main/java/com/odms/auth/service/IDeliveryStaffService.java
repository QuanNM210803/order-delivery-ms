package com.odms.auth.service;

import com.odms.auth.dto.response.DeliveryStaffResponse;
import nmquan.commonlib.dto.response.IDResponse;

import java.util.List;

public interface IDeliveryStaffService {
    Boolean getMyStatusFindingOrder();
    IDResponse<Long> updateStatusFindingOrder(Long userId);
    List<DeliveryStaffResponse> findDeliveryStaff(Boolean status);
}
