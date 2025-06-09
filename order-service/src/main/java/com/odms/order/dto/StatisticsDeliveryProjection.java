package com.odms.order.dto;

public interface StatisticsDeliveryProjection {
    Integer getDeliveryStaffId();
    Integer getOrderCompleted();
    Integer getOrderCancelled();
    Integer getOrderPending();
    Double getShippingFeeTotal();
    Integer getRanking();
}
