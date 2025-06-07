package com.odms.delivery.service.impl;

import com.odms.delivery.repository.DeliveryOrderRepository;
import com.odms.delivery.service.IOrderFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFilterServiceImpl implements IOrderFilterService {
    private final DeliveryOrderRepository orderRepository;
}
