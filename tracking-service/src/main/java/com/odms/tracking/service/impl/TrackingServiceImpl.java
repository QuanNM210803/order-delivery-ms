package com.odms.tracking.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odms.tracking.dto.event.Order;
import com.odms.tracking.dto.event.StatusHistory;
import com.odms.tracking.dto.event.UpdateDeliveryStatusEvent;
import com.odms.tracking.dto.request.internal.IdListRequest;
import com.odms.tracking.dto.response.OrderResponse;
import com.odms.tracking.dto.response.Response;
import com.odms.tracking.dto.response.StatusHistoryResponse;
import com.odms.tracking.dto.response.internal.DeliveryInfo;
import com.odms.tracking.dto.response.internal.UserResponse;
import com.odms.tracking.exception.AppException;
import com.odms.tracking.exception.ErrorCode;
import com.odms.tracking.service.ITrackingService;
import com.odms.tracking.utils.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements ITrackingService {
    private final RedisTemplate<String, Object> redisTemplate;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${server.host_delivery_service}")
    private String HOST_DELIVERY_SERVICE;

    @Value("${server.port_delivery_service}")
    private String PORT_DELIVERY_SERVICE;

    @Value("${server.host_order_service}")
    private String HOST_ORDER_SERVICE;

    @Value("${server.port_order_service}")
    private String PORT_ORDER_SERVICE;

    @Value("${server.host_auth_service}")
    private String AUTH_SERVICE_HOST;

    @Value("${server.port_auth_service}")
    private String AUTH_SERVICE_PORT;

    @Value("${jwt.x-internal-token}")
    private String X_INTERNAL_TOKEN;

    @Override
    public OrderResponse getOrderDetails(String orderCode, String phone) {
        String key = "order:" + orderCode;

        Order order = new Order();
        Object raw = redisTemplate.opsForValue().get(key);
        if (raw != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            order = objectMapper.convertValue(raw, Order.class);
        } else {
            DeliveryInfo deliveryInfo = this.getStatusHistory(orderCode);
            order = this.getOrderInfo(orderCode);

            // missing senderName and senderPhone
            Integer customerId = order.getCustomerId();
            Map<Integer, UserResponse> userInfo = this.getUserInfo(List.of(customerId));

            order.setSenderName(userInfo.get(customerId).getFullName());
            order.setSenderPhone(userInfo.get(customerId).getPhone());
            order.setStatusHistory(deliveryInfo.getStatusHistory());
            order.setDeliveryStaffId(deliveryInfo.getDeliveryStaffId());
            this.saveOrder(order);
        }

        // for API public: require phone to access
        if (phone != null && !phone.isEmpty()) {
            if (!order.getReceiverPhone().equals(phone) && !order.getSenderPhone().equals(phone)) {
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }
        }

        List<String> roles = WebUtils.getRoles();
        Integer userId = WebUtils.getCurrentUserId();
        if (roles.contains("CUSTOMER")) {
            if(!Objects.equals(userId, order.getCustomerId())){
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }
        } else if(roles.contains("DELIVERY_STAFF")) {
            if(!Objects.equals(userId, order.getDeliveryStaffId())){
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }
        }
        return this.mapToOrderResponse(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processOrderCreation(String message) {
        Order order = this.toObject(message, Order.class);
        this.saveOrder(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processOrderUpdate(String message) {
        UpdateDeliveryStatusEvent updateDeliveryStatusEvent = this.toObject(message, UpdateDeliveryStatusEvent.class);
        String orderCode = updateDeliveryStatusEvent.getOrderCode();

        String key = "order:" + orderCode;
        Object raw = redisTemplate.opsForValue().get(key);
        if (raw != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Order order = objectMapper.convertValue(raw, Order.class);

            order.getStatusHistory().add(updateDeliveryStatusEvent.getStatusHistory());
            order.setDeliveryStaffId(updateDeliveryStatusEvent.getDeliveryStaffId());
            this.saveOrder(order);
        }
    }

    private void saveOrder(Order order) {
        String key = "order:" + order.getOrderCode();
        Duration ttl = Duration.ofHours(24);
        redisTemplate.opsForValue().set(key, order, ttl);
    }

    @SneakyThrows
    private <T> T toObject(String message, Class<T> type) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(message, type);
    }

    // call delivery service
    private DeliveryInfo getStatusHistory(String orderCode) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Internal-Token", X_INTERNAL_TOKEN);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ParameterizedTypeReference<Response<DeliveryInfo>> typeRef =
                    new ParameterizedTypeReference<>() {};
            ResponseEntity<Response<DeliveryInfo>> response = restTemplate.exchange(
                    "http://" + HOST_DELIVERY_SERVICE + ":" + PORT_DELIVERY_SERVICE + "/delivery/delivery-order/internal/status-history/{orderCode}",
                    HttpMethod.GET,
                    entity,
                    typeRef,
                    orderCode
            );
            return Objects.requireNonNull(response.getBody()).getData();
        } catch (Exception exception) {
            this.throwException(exception.getMessage());
            return null;
        }
    }

    // call order-service
    private Order getOrderInfo(String orderCode) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-Token", X_INTERNAL_TOKEN);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ParameterizedTypeReference<Response<Order>> typeRef =
                    new ParameterizedTypeReference<>() {};
            ResponseEntity<Response<Order>> response = restTemplate.exchange(
                    "http://" + HOST_ORDER_SERVICE + ":" + PORT_ORDER_SERVICE + "/order/order/internal/order/{orderCode}",
                    HttpMethod.GET,
                    entity,
                    typeRef,
                    orderCode
            );
            return Objects.requireNonNull(response.getBody()).getData();
        } catch (Exception exception) {
            this.throwException(exception.getMessage());
            return null;
        }
    }

    // call auth-service
    private Map<Integer, UserResponse> getUserInfo(List<Integer> ids){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Internal-Token", X_INTERNAL_TOKEN);
            HttpEntity<IdListRequest> entity = new HttpEntity<>(IdListRequest.builder()
                    .ids(ids)
                    .build(), headers);
            ParameterizedTypeReference<Response<Map<Integer, UserResponse>>> typeRef =
                    new ParameterizedTypeReference<>() {};
            ResponseEntity<Response<Map<Integer, UserResponse>>> response = restTemplate.exchange(
                    "http://" + AUTH_SERVICE_HOST + ":" + AUTH_SERVICE_PORT + "/auth/user/internal/info/users",
                    HttpMethod.POST,
                    entity,
                    typeRef
            );
            return Objects.requireNonNull(response.getBody()).getData();
        } catch (Exception exception) {
            this.throwException(exception.getMessage());
            return null;
        }
    }

    private OrderResponse mapToOrderResponse(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        List<StatusHistory> statusHistory = order.getStatusHistory();
        List<StatusHistoryResponse> statusHistoryResponses = statusHistory.stream()
                .map(status -> StatusHistoryResponse.builder()
                        .status(status.getStatus())
                        .createdBy(status.getCreatedBy())
                        .updatedAt(status.getUpdatedAt().format(formatter))
                        .build())
                .toList();
        return OrderResponse.builder()
                .orderCode(order.getOrderCode())
                .senderName(order.getSenderName())
                .senderPhone(order.getSenderPhone())
                .pickupAddress(order.getPickupAddress())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .deliveryAddress(order.getDeliveryAddress())
                .price(this.formatCurrency(order.getPrice()))
                .shippingFee(this.formatCurrency(order.getShippingFee()))
                .description(order.getDescription())
                .note(order.getNote())
                .weight(this.normalize(order.getWeight()) + " Kg")
                .size(order.getSize())
                .distance(this.normalize(order.getDistance()) + " Km")
                .statusHistory(statusHistoryResponses)
                .build();
    }

    private Number normalize(Double value) {
        double result = value / 1000;
        if (Math.floor(result) == result) {
            return (int) result;
        } else {
            return Math.round(result * 1000.0) / 1000.0;
        }
    }

    private String formatCurrency(Double value) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(value) + " VND";
    }

    @SneakyThrows
    private void throwException(String message) {
        String pre = message.split(":", 2)[1].trim();
        String formattedMessage = pre.substring(1, pre.length()-1).trim();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Integer statusCode = Integer.valueOf(message.split(":", 2)[0].trim());
        Response<Object> response = objectMapper.readValue(formattedMessage,  new TypeReference<Response<Object>>() {});
        throw new AppException(response, statusCode);
    }
}
