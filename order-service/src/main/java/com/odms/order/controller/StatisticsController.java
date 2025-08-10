package com.odms.order.controller;

import com.odms.order.dto.response.StatisticsDeliveryResponse;
import com.odms.order.service.IStatisticsService;
import lombok.RequiredArgsConstructor;
import nmquan.commonlib.dto.response.Response;
import nmquan.commonlib.utils.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticsController {
    private final IStatisticsService statisticsService;

    @GetMapping("/delivery_staff")
    @PreAuthorize("hasAuthority('DELIVERY_STAFF')")
    public ResponseEntity<Response<StatisticsDeliveryResponse>> getDeliveryOrderStatistics(@RequestParam(required = false) LocalDate startDate,
                                                                                           @RequestParam(required = false) LocalDate endDate) {
        StatisticsDeliveryResponse response = statisticsService.getDeliveryOrderStatistics(startDate, endDate);
        return ResponseUtils.success(response);
    }
}
