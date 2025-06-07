package com.odms.order.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odms.order.dto.event.OrderCreateEvent;
import com.odms.order.dto.event.OrderCreateEventTracking;
import com.odms.order.dto.event.StatusHistory;
import com.odms.order.dto.request.OrderRequest;
import com.odms.order.dto.response.IDResponse;
import com.odms.order.dto.response.OrderResponse;
import com.odms.order.entity.Order;
import com.odms.order.entity.enumerate.OrderStatus;
import com.odms.order.exception.AppException;
import com.odms.order.exception.ErrorCode;
import com.odms.order.repository.OrderRepository;
import com.odms.order.service.IGeoService;
import com.odms.order.service.IOrderService;
import com.odms.order.service.IShippingFeeService;
import com.odms.order.utils.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final IShippingFeeService shippingFeeService;
    private final IGeoService geoService;
    private final OrderRepository orderRepository;

    @SneakyThrows
    @Override
    public IDResponse<String> createOrder(OrderRequest orderRequest) {
        String orderCode = this.generateOrderCode();
        Integer customerId = WebUtils.getCurrentUserId();
        Double weight = orderRequest.getWeight()*1000; // Convert kg to grams
        Double distance = geoService.getDistance(orderRequest.getPickupAddress(), orderRequest.getDeliveryAddress());
        Double shippingFee = shippingFeeService.calculateShippingFee(distance, weight);

        Order order = Order.builder()
                .orderCode(orderCode)
                .customerId(customerId)
                .receiverName(orderRequest.getReceiverName())
                .receiverPhone(orderRequest.getReceiverPhone())
                .pickupAddress(orderRequest.getPickupAddress())
                .deliveryAddress(orderRequest.getDeliveryAddress())
                .description(orderRequest.getDescription())
                .size(orderRequest.getSize())
                .weight(weight)
                .note(orderRequest.getNote())
                .price(orderRequest.getPrice())
                .distance(distance)
                .shippingFee(shippingFee)
                .build();
        orderRepository.save(order);

        // SEND TO DELIVERY SERVICE
        String fullName = WebUtils.getCurrentFullName();
        OrderCreateEvent orderCreateEvent = OrderCreateEvent.builder()
                .id(UUID.randomUUID().toString())
                .orderCode(order.getOrderCode())
                .deliveryStaffId(null)
                .senderName(fullName)
                .pickupAddress(order.getPickupAddress())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .deliveryAddress(order.getDeliveryAddress())
                .description(order.getDescription())
                .shippingFee(shippingFee)
                .createdAt(order.getCreatedAt())
                .createdBy(customerId)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String orderCreateEventJson = objectMapper.writeValueAsString(orderCreateEvent);
        kafkaTemplate.send("order-create-topic", orderCreateEventJson);

        //SEND TO TRACKING SERVICE
        String phone = WebUtils.getCurrentPhone();
        OrderCreateEventTracking orderCreateEventTracking = OrderCreateEventTracking.builder()
                .orderCode(order.getOrderCode())
                .senderName(fullName)
                .senderPhone(phone)
                .pickupAddress(order.getPickupAddress())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .deliveryAddress(order.getDeliveryAddress())
                .price(order.getPrice())
                .shippingFee(shippingFee)
                .description(order.getDescription())
                .note(order.getNote())
                .weight(weight)
                .size(order.getSize())
                .distance(distance)
                .statusHistory(List.of(StatusHistory.builder()
                                .status(OrderStatus.CREATED.getDescription())
                                .createdBy(fullName)
                                .updatedAt(order.getCreatedAt())
                        .build()))
                .customerId(customerId)
                .build();
        String orderCreateEventTrackingJson = objectMapper.writeValueAsString(orderCreateEventTracking);
        kafkaTemplate.send("order-create-tracking-topic", orderCreateEventTrackingJson);

        return IDResponse.<String>builder()
                .id(order.getOrderCode())
                .build();
    }

    @Override
    public boolean checkCustomerId(Integer customerId, String orderCode) {
        Optional<Order> order = orderRepository.findByOrderCode(orderCode);
        return order.map(value -> value.getCustomerId().equals(customerId)).orElse(false);
    }

    @Override
    public OrderResponse getOrderByOrderCode(String orderCode) {
        Optional<Order> orderOptional = orderRepository.findByOrderCode(orderCode);
        if(orderOptional.isEmpty()){
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
        Order order = orderOptional.get();
        return OrderResponse.builder()
                .orderCode(order.getOrderCode())
                .customerId(order.getCustomerId())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .pickupAddress(order.getPickupAddress())
                .deliveryAddress(order.getDeliveryAddress())
                .description(order.getDescription())
                .size(order.getSize())
                .weight(order.getWeight())
                .note(order.getNote())
                .price(order.getPrice())
                .distance(order.getDistance())
                .shippingFee(order.getShippingFee())
                .build();
    }

    private String generateOrderCode() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHH"));
        String random = this.generateRandomCode(4);
        return "ORD" + timePart + random;
    }

    private String generateRandomCode(int length) {
        String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

}
