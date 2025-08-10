package com.odms.order.service.impl;

import com.odms.order.dto.event.OrderCreateEvent;
import com.odms.order.dto.event.OrderCreateEventTracking;
import com.odms.order.dto.event.StatusHistory;
import com.odms.order.dto.event.UpdateDeliveryStatusEvent;
import com.odms.order.dto.request.FilterOrderAdmin;
import com.odms.order.dto.request.FilterOrderCustomer;
import com.odms.order.dto.request.FilterOrderDelivery;
import com.odms.order.dto.request.OrderRequest;
import com.odms.order.dto.response.OrderFilterResponse;
import com.odms.order.dto.response.OrderResponse;
import com.odms.order.entity.Order;
import com.odms.order.enums.OrderErrorCode;
import com.odms.order.enums.OrderStatus;
import com.odms.order.repository.OrderRepository;
import com.odms.order.service.IGeoService;
import com.odms.order.service.IOrderService;
import com.odms.order.service.IShippingFeeService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nmquan.commonlib.dto.PageInfo;
import nmquan.commonlib.dto.response.FilterResponse;
import nmquan.commonlib.dto.response.IDResponse;
import nmquan.commonlib.exception.AppException;
import nmquan.commonlib.utils.ObjectMapperUtils;
import nmquan.commonlib.utils.WebUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

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
        Long customerId = WebUtils.getCurrentUserId();
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
                .orderStatus(OrderStatus.CREATED.name())
                .senderName(WebUtils.getCurrentFullName())
                .build();
        orderRepository.save(order);

        // SEND TO DELIVERY SERVICE
        String fullName = WebUtils.getCurrentFullName();
        OrderCreateEvent orderCreateEvent = OrderCreateEvent.builder()
                .id(UUID.randomUUID().toString())
                .orderCode(order.getOrderCode())
                .deliveryStaffId(null)
                .createdAt(order.getCreatedAt())
                .createdBy(customerId)
                .build();
        String orderCreateEventJson = ObjectMapperUtils.convertToJson(orderCreateEvent);
        kafkaTemplate.send("order-create-topic", orderCreateEventJson);

        //SEND TO TRACKING SERVICE
        String phone = com.odms.order.utils.WebUtils.getCurrentPhone();
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
        String orderCreateEventTrackingJson = ObjectMapperUtils.convertToJson(orderCreateEventTracking);
        kafkaTemplate.send("order-create-tracking-topic", orderCreateEventTrackingJson);

        return IDResponse.<String>builder()
                .id(order.getOrderCode())
                .build();
    }

    @Override
    public boolean checkCustomerId(Long customerId, String orderCode) {
        Optional<Order> order = orderRepository.findByOrderCode(orderCode, false);
        return order.map(value -> value.getCustomerId().equals(customerId)).orElse(false);
    }

    @Override
    public OrderResponse getOrderByOrderCode(String orderCode) {
        Optional<Order> orderOptional = orderRepository.findByOrderCode(orderCode, false);
        if(orderOptional.isEmpty()){
            throw new AppException(OrderErrorCode.ORDER_NOT_FOUND);
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

    @Override
    public void updateStatusDelivery(UpdateDeliveryStatusEvent request) {
        Optional<Order> orderOptional = orderRepository.findByOrderCode(request.getOrderCode(), false);
        if(orderOptional.isEmpty()){
            throw new AppException(OrderErrorCode.ORDER_NOT_FOUND);
        }
        Order order = orderOptional.get();
        order.setOrderStatus(request.getStatus().name());
        if(request.getStatus() == OrderStatus.ASSIGNED) {
            order.setDeliveryStaffId(request.getDeliveryStaffId());
        }
        orderRepository.save(order);
    }

    @Override
    public FilterResponse<OrderFilterResponse> filterByCustomer(FilterOrderCustomer request) {
        int pageIndex = request.getPageIndex() != null ? request.getPageIndex() - 1 : 0;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        Long customerId = WebUtils.getCurrentUserId();
        String orderCode = StringUtils.isNoneBlank(request.getOrderCode()) ? request.getOrderCode().trim() : null;
        String receiverName = StringUtils.isNoneBlank(request.getReceiverName()) ? request.getReceiverName().trim().toLowerCase() : null;
        String receiverPhone = StringUtils.isNoneBlank(request.getReceiverPhone()) ? request.getReceiverPhone().trim() : null;
        List<OrderStatus> orderStatuses = request.getOrderStatuses();

        LocalDateTime startDate = request.getStartDate()!=null ? request.getStartDate().atStartOfDay() : null;
        LocalDateTime endDate = request.getEndDate()!=null ? request.getEndDate().atTime(23, 59, 59) : null;

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

        Page<Order> orderPage = orderRepository.filterByCustomer(
                customerId,
                orderCode,
                receiverName,
                receiverPhone,
                orderStatuses,
                startDate,
                endDate,
                false,
                pageable
        );
        PageInfo pageInfo = PageInfo.builder()
                .pageIndex(orderPage.getNumber() + 1)
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .hasNextPage(orderPage.hasNext())
                .build();
        List<Order> orders = orderPage.getContent();
        List<OrderFilterResponse> orderFilterResponses = orders.stream()
                .map(order -> {
                    OrderStatus orderStatus = OrderStatus.getByName(order.getOrderStatus());
                    return OrderFilterResponse.builder()
                            .orderCode(order.getOrderCode())
                            .receiverName(order.getReceiverName())
                            .receiverPhone(order.getReceiverPhone())
                            .deliveryAddress(order.getDeliveryAddress())
                            .description(order.getDescription())
                            .shippingFee(formatCurrency(order.getShippingFee()))
                            .orderStatus(orderStatus != null ? orderStatus.getDescription() : null)
                            .build();
                })
                .toList();
        return FilterResponse.<OrderFilterResponse>builder()
                .data(orderFilterResponses)
                .pageInfo(pageInfo)
                .build();
    }

    @Override
    public FilterResponse<OrderFilterResponse> filterByDelivery(FilterOrderDelivery request) {
        int pageIndex = request.getPageIndex() != null ? request.getPageIndex() - 1 : 0;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        Long deliveryStaffId = WebUtils.getCurrentUserId();
        String orderCode = StringUtils.isNoneBlank(request.getOrderCode()) ? request.getOrderCode().trim() : null;
        String senderName = StringUtils.isNoneBlank(request.getSenderName()) ? request.getSenderName().trim().toLowerCase() : null;
        String description = StringUtils.isNoneBlank(request.getDescription()) ? request.getDescription().trim().toLowerCase() : null;

        List<String> orderStatuses = null;
        OrderStatus status = request.getStatus();
        if(status == OrderStatus.COMPLETED || status == OrderStatus.CANCELLED) {
            orderStatuses = Stream.of(status)
                                .map(OrderStatus::name)
                        .toList();
        } else {
            orderStatuses = Stream.of(OrderStatus.ASSIGNED, OrderStatus.PICKED_UP, OrderStatus.IN_TRANSIT, OrderStatus.DELIVERED)
                    .map(OrderStatus::name)
                    .toList();
        }

        LocalDateTime startDate = request.getStartDate()!=null ? request.getStartDate().atStartOfDay() : null;
        LocalDateTime endDate = request.getEndDate()!=null ? request.getEndDate().atTime(23, 59, 59) : null;

        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

        Page<Order> orderPage = orderRepository.filterByDelivery(
                deliveryStaffId,
                orderCode,
                senderName,
                description,
                orderStatuses,
                startDate,
                endDate,
                false,
                pageable
        );
        PageInfo pageInfo = PageInfo.builder()
                .pageIndex(orderPage.getNumber() + 1)
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .hasNextPage(orderPage.hasNext())
                .build();
        List<Order> orders = orderPage.getContent();
        List<OrderFilterResponse> orderFilterResponses = orders.stream()
                .map(order -> {
                    OrderStatus orderStatus = OrderStatus.getByName(order.getOrderStatus());
                    return OrderFilterResponse.builder()
                            .orderCode(order.getOrderCode())
                            .senderName(order.getSenderName())
                            .pickupAddress(order.getPickupAddress())
                            .deliveryAddress(order.getDeliveryAddress())
                            .description(order.getDescription())
                            .orderStatus(orderStatus != null ? orderStatus.getDescription() : null)
                            .build();
                })
                .toList();
        return FilterResponse.<OrderFilterResponse>builder()
                .data(orderFilterResponses)
                .pageInfo(pageInfo)
                .build();
    }

    @Override
    public FilterResponse<OrderFilterResponse> filterByAdmin(FilterOrderAdmin request) {
        int pageIndex = request.getPageIndex() != null ? request.getPageIndex() - 1 : 0;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        String orderCode = StringUtils.isNoneBlank(request.getOrderCode()) ? request.getOrderCode().trim() : null;
        String senderName = StringUtils.isNoneBlank(request.getSenderName()) ? request.getSenderName().trim().toLowerCase() : null;
        String description = StringUtils.isNoneBlank(request.getDescription()) ? request.getDescription().trim().toLowerCase() : null;
        List<OrderStatus> orderStatuses = request.getOrderStatuses();

        LocalDateTime startDate = request.getStartDate()!=null ? request.getStartDate().atStartOfDay() : null;
        LocalDateTime endDate = request.getEndDate()!=null ? request.getEndDate().atTime(23, 59, 59) : null;

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

        Page<Order> orderPage = orderRepository.filterByAdmin(
                orderCode,
                senderName,
                description,
                orderStatuses,
                startDate,
                endDate,
                false,
                pageable
        );
        PageInfo pageInfo = PageInfo.builder()
                .pageIndex(orderPage.getNumber() + 1)
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .hasNextPage(orderPage.hasNext())
                .build();
        List<Order> orders = orderPage.getContent();
        List<OrderFilterResponse> orderFilterResponses = orders.stream()
                .map(order -> {
                    OrderStatus orderStatus = OrderStatus.getByName(order.getOrderStatus());
                    return OrderFilterResponse.builder()
                            .orderCode(order.getOrderCode())
                            .senderName(order.getSenderName())
                            .pickupAddress(order.getPickupAddress())
                            .deliveryAddress(order.getDeliveryAddress())
                            .description(order.getDescription())
                            .orderStatus(orderStatus != null ? orderStatus.getDescription() : null)
                            .build();
                })
                .toList();
        return FilterResponse.<OrderFilterResponse>builder()
                .data(orderFilterResponses)
                .pageInfo(pageInfo)
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

    private String formatCurrency(Double value) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(value) + " VND";
    }

}
