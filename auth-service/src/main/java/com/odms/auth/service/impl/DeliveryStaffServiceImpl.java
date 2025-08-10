package com.odms.auth.service.impl;

import com.odms.auth.dto.response.DeliveryStaffResponse;
import com.odms.auth.entity.DeliveryStaff;
import com.odms.auth.enums.AuthErrorCode;
import com.odms.auth.repository.DeliveryStaffRepository;
import com.odms.auth.service.IDeliveryStaffService;
import lombok.RequiredArgsConstructor;
import nmquan.commonlib.dto.response.IDResponse;
import nmquan.commonlib.exception.AppException;
import nmquan.commonlib.exception.CommonErrorCode;
import nmquan.commonlib.utils.WebUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryStaffServiceImpl implements IDeliveryStaffService {

    private final DeliveryStaffRepository deliveryStaffRepository;

    @Override
    public Boolean getMyStatusFindingOrder() {
        DeliveryStaff deliveryStaff = deliveryStaffRepository.findByUserId(WebUtils.getCurrentUserId(), false)
                .orElseThrow(() -> new AppException(CommonErrorCode.UNAUTHENTICATED));
        return deliveryStaff.getFindingOrder();
    }

    @Override
    public IDResponse<Long> updateStatusFindingOrder(Long userId) {
        if(userId == null) {
            DeliveryStaff deliveryStaff = deliveryStaffRepository.findByUserId(WebUtils.getCurrentUserId(), false)
                    .orElseThrow(() -> new AppException(AuthErrorCode.UPDATE_STATUS_FINDING_ORDER_FAILED));

            deliveryStaff.setFindingOrder(!deliveryStaff.getFindingOrder());
            deliveryStaffRepository.save(deliveryStaff);
            return IDResponse.<Long>builder()
                    .id(WebUtils.getCurrentUserId())
                    .build();
        } else {
            DeliveryStaff deliveryStaff = deliveryStaffRepository.findByUserId(userId, false)
                    .orElseThrow(() -> new AppException(AuthErrorCode.UPDATE_STATUS_FINDING_ORDER_FAILED));
            if(!deliveryStaff.getFindingOrder()){
                throw new AppException(CommonErrorCode.ERROR);
            }
            deliveryStaff.setFindingOrder(false);
            deliveryStaffRepository.save(deliveryStaff);
            return IDResponse.<Long>builder()
                    .id(userId)
                    .build();
        }
    }

    @Override
    public List<DeliveryStaffResponse> findDeliveryStaff(Boolean status) {
        List<DeliveryStaff> deliveryStaffList = deliveryStaffRepository.findByFindingOrder(status, false);
        return deliveryStaffList.stream().map(ds -> DeliveryStaffResponse.builder()
                        .userId(ds.getUser().getId())
                        .fullName(ds.getUser().getFullName())
                        .phone(ds.getUser().getPhone())
                    .build()
        ).toList();
    }
}
