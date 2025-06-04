package com.odms.auth.service;

import com.odms.auth.dto.response.DeliveryStaffResponse;
import com.odms.auth.dto.response.IDResponse;

import java.util.List;

public interface IDeliveryStaffService {
    Boolean getMyStatusFindingOrder();
    IDResponse<Integer> updateStatusFindingOrder(Integer userId);
    List<DeliveryStaffResponse> findDeliveryStaff(Boolean status);
}
