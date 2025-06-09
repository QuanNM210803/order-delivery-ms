package com.odms.order.service;

import com.odms.order.dto.response.StatisticsDeliveryResponse;

import java.time.LocalDate;

public interface IStatisticsService {
    StatisticsDeliveryResponse getDeliveryOrderStatistics(LocalDate startDate, LocalDate endDate);
}
