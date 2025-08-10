package com.odms.order.service.impl;

import com.odms.order.dto.response.StatisticsDeliveryResponse;
import com.odms.order.repository.OrderRepository;
import com.odms.order.service.IStatisticsService;
import lombok.RequiredArgsConstructor;
import nmquan.commonlib.utils.WebUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements IStatisticsService {
    private final OrderRepository orderRepository;

    @Override
    public StatisticsDeliveryResponse getDeliveryOrderStatistics(LocalDate startDate, LocalDate endDate) {
        Long deliveryStaffId = WebUtils.getCurrentUserId();
        LocalDateTime startDateTime = startDate!=null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate!=null ? endDate.atTime(23, 59, 59) : null;
        return orderRepository.getStatisticsForDeliveryStaff(deliveryStaffId, startDateTime, endDateTime)
                .map(p -> {
                    StatisticsDeliveryResponse response = new StatisticsDeliveryResponse(
                            p.getOrderCompleted(),
                            p.getOrderCancelled(),
                            p.getOrderPending(),
                            p.getShippingFeeTotal(),
                            p.getRanking());
                    response.setOrderTotal();
                    response.setCommission();
                    response.setRankBonus();
                    response.setRevenueBonus();
                    response.setEarnings();
                    return response;
                })
                .orElse(new StatisticsDeliveryResponse(0L,0L,0L,0L,0.,
                        0.,0L,0.,0.,0.));
    }
}
