package com.odms.order.dto;

public interface StatisticsDeliveryProjection {
    Long getDeliveryStaffId();
    Long getOrderCompleted();
    Long getOrderCancelled();
    Long getOrderPending();
    Double getShippingFeeTotal();
    Long getRanking();
}
