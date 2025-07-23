package com.odms.auth.service.impl;

import com.odms.auth.dto.response.DeliveryStaffResponse;
import com.odms.auth.dto.response.IDResponse;
import com.odms.auth.entity.DeliveryStaff;
import com.odms.auth.entity.User;
import com.odms.auth.exception.AppException;
import com.odms.auth.exception.ErrorCode;
import com.odms.auth.repository.DeliveryStaffRepository;
import com.odms.auth.service.IDeliveryStaffService;
import com.odms.auth.utils.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryStaffServiceImpl implements IDeliveryStaffService {

    private final DeliveryStaffRepository deliveryStaffRepository;

    @Override
    public Boolean getMyStatusFindingOrder() {
        User user = WebUtils.getCurrentUser();
        DeliveryStaff deliveryStaff = deliveryStaffRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        return deliveryStaff.getFindingOrder();
    }

    @Override
    public IDResponse<Integer> updateStatusFindingOrder(Integer userId) {
        if(userId == null) {
            User user = WebUtils.getCurrentUser();
            DeliveryStaff deliveryStaff = deliveryStaffRepository.findByUserId(user.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.UPDATE_STATUS_FINDING_ORDER_FAILED));

            deliveryStaff.setFindingOrder(!deliveryStaff.getFindingOrder());
            deliveryStaffRepository.save(deliveryStaff);
            return IDResponse.<Integer>builder()
                    .id(user.getUserId())
                    .build();
        } else {
            DeliveryStaff deliveryStaff = deliveryStaffRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.UPDATE_STATUS_FINDING_ORDER_FAILED));
            if(!deliveryStaff.getFindingOrder()){
                throw new AppException(ErrorCode.ERROR);
            }
            deliveryStaff.setFindingOrder(false);
            deliveryStaffRepository.save(deliveryStaff);

            return IDResponse.<Integer>builder()
                    .id(userId)
                    .build();
        }
    }

    @Override
    public List<DeliveryStaffResponse> findDeliveryStaff(Boolean status) {
        List<DeliveryStaff> deliveryStaffList = deliveryStaffRepository.findByFindingOrder(status);
        return deliveryStaffList.stream().map(ds -> DeliveryStaffResponse.builder()
                        .userId(ds.getUser().getUserId())
                        .fullName(ds.getUser().getFullName())
                        .phone(ds.getUser().getPhone())
                    .build()
        ).toList();
    }
}
