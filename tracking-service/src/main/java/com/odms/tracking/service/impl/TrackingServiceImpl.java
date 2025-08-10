package com.odms.tracking.service.impl;

import com.odms.tracking.dto.event.Order;
import com.odms.tracking.dto.event.StatusHistory;
import com.odms.tracking.dto.event.UpdateDeliveryStatusEvent;
import com.odms.tracking.dto.request.internal.IdListRequest;
import com.odms.tracking.dto.response.OrderResponse;
import com.odms.tracking.dto.response.StatusHistoryResponse;
import com.odms.tracking.dto.response.internal.DeliveryInfo;
import com.odms.tracking.dto.response.internal.UserResponse;
import com.odms.tracking.service.ITrackingService;
import nmquan.commonlib.constant.CommonConstants;
import nmquan.commonlib.dto.response.Response;
import nmquan.commonlib.exception.AppException;
import nmquan.commonlib.exception.CommonErrorCode;
import nmquan.commonlib.service.RestTemplateService;
import nmquan.commonlib.utils.DateUtils;
import nmquan.commonlib.utils.ObjectMapperUtils;
import nmquan.commonlib.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TrackingServiceImpl implements ITrackingService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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

    @Autowired
    private RestTemplateService restTemplateService;

    @Override
    public OrderResponse getOrderDetails(String orderCode, String phone) {
        String key = "order:" + orderCode;

        Order order = new Order();
        Object raw = redisTemplate.opsForValue().get(key);
        if (raw != null) {
            order = ObjectMapperUtils.convertToObject(raw, Order.class);
        } else {
            DeliveryInfo deliveryInfo = this.getStatusHistory(orderCode);
            order = this.getOrderInfo(orderCode);

            // missing senderName and senderPhone
            Long customerId = order.getCustomerId();
            Map<Long, UserResponse> userInfo = this.getUserInfo(List.of(customerId));

            order.setSenderName(userInfo.get(customerId).getFullName());
            order.setSenderPhone(userInfo.get(customerId).getPhone());
            order.setStatusHistory(deliveryInfo.getStatusHistory());
            order.setDeliveryStaffId(deliveryInfo.getDeliveryStaffId());
            this.saveOrder(order);
        }

        // for API public: require phone to access
        if (phone != null && !phone.isEmpty()) {
            if (!order.getReceiverPhone().equals(phone) && !order.getSenderPhone().equals(phone)) {
                throw new AppException(CommonErrorCode.ACCESS_DENIED);
            }
            return this.mapToOrderResponse(order);
        }

        List<String> roles = WebUtils.getCurrentRole();
        Long userId = WebUtils.getCurrentUserId();
        if (roles.contains("CUSTOMER")) {
            if(!Objects.equals(userId, order.getCustomerId())){
                throw new AppException(CommonErrorCode.ACCESS_DENIED);
            }
        } else if(roles.contains("DELIVERY_STAFF")) {
            if(!Objects.equals(userId, order.getDeliveryStaffId())){
                throw new AppException(CommonErrorCode.ACCESS_DENIED);
            }
        }
        return this.mapToOrderResponse(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processOrderCreation(String message) {
        Order order = ObjectMapperUtils.convertToObject(message, Order.class);
        this.saveOrder(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processOrderUpdate(String message) {
        UpdateDeliveryStatusEvent updateDeliveryStatusEvent = ObjectMapperUtils.convertToObject(message, UpdateDeliveryStatusEvent.class);
        String orderCode = updateDeliveryStatusEvent.getOrderCode();

        String key = "order:" + orderCode;
        Object raw = redisTemplate.opsForValue().get(key);
        if (raw != null) {
            Order order = ObjectMapperUtils.convertToObject(raw, Order.class);
            order.getStatusHistory().add(updateDeliveryStatusEvent.getStatusHistory());
            if(updateDeliveryStatusEvent.getDeliveryStaffId() != null){
                order.setDeliveryStaffId(updateDeliveryStatusEvent.getDeliveryStaffId());
            }
            this.saveOrder(order);
        }
    }

    private void saveOrder(Order order) {
        String key = "order:" + order.getOrderCode();
        Duration ttl = Duration.ofHours(24);
        redisTemplate.opsForValue().set(key, order, ttl);
    }

    // call delivery service
    private DeliveryInfo getStatusHistory(String orderCode) {
        String url = "http://" + HOST_DELIVERY_SERVICE + ":" + PORT_DELIVERY_SERVICE + "/delivery/delivery-order/internal/status-history/{orderCode}";
        Response<DeliveryInfo> response = restTemplateService.getMethodRestTemplate(
                url,
                new ParameterizedTypeReference<Response<DeliveryInfo>>() {},
                orderCode
        );
        return response != null ? response.getData() : null;
    }

    // call order-service
    private Order getOrderInfo(String orderCode) {
        String url = "http://" + HOST_ORDER_SERVICE + ":" + PORT_ORDER_SERVICE + "/order/order/internal/order/{orderCode}";
        Response<Order> response = restTemplateService.getMethodRestTemplate(
                url,
                new ParameterizedTypeReference<Response<Order>>() {},
                orderCode
        );
        return response != null ? response.getData() : null;
    }

    // call auth-service
    private Map<Long, UserResponse> getUserInfo(List<Long> ids){
        String url = "http://" + AUTH_SERVICE_HOST + ":" + AUTH_SERVICE_PORT + "/auth/user/internal/info/users";
        Response<Map<Long, UserResponse>> response = restTemplateService.postMethodRestTemplate(
                url,
                new ParameterizedTypeReference<Response<Map<Long, UserResponse>>>() {},
                IdListRequest.builder()
                    .ids(ids)
                    .build()
        );
        return response != null ? response.getData() : null;
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<StatusHistory> statusHistory = order.getStatusHistory();
        List<StatusHistoryResponse> statusHistoryResponses = statusHistory.stream()
                .map(status -> StatusHistoryResponse.builder()
                        .status(status.getStatus())
                        .createdBy(status.getCreatedBy())
                        .updatedAt(
                                DateUtils.instantToString_HCM(status.getUpdatedAt(), CommonConstants.DATE_TIME.DD_MM_YYYY_HH_MM_SS_HYPHEN)
                        )
                        .reasonCancel(status.getReasonCancel())
                        .noteCancel(status.getNoteCancel())
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
            return Math.round(result * 10.0) / 10.0;
        }
    }

    private String formatCurrency(Double value) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(value) + " VND";
    }
}
