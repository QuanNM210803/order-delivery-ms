package com.odms.delivery.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odms.delivery.document.DeliveryOrder;
import com.odms.delivery.document.StatusHistory;
import com.odms.delivery.document.enumerate.OrderStatus;
import com.odms.delivery.document.enumerate.ReasonCancel;
import com.odms.delivery.dto.TypeMail;
import com.odms.delivery.dto.event.NotificationEvent;
import com.odms.delivery.dto.event.OrderCreateEvent;
import com.odms.delivery.dto.event.UpdateDeliveryStatusEvent;
import com.odms.delivery.dto.event.UpdateDeliveryStatusToOrderEvent;
import com.odms.delivery.dto.request.UpdateDeliveryStatusRequest;
import com.odms.delivery.dto.request.internal.IdListRequest;
import com.odms.delivery.dto.response.IDResponse;
import com.odms.delivery.dto.response.Response;
import com.odms.delivery.dto.response.internal.DeliveryInfo;
import com.odms.delivery.dto.response.internal.OrderResponse;
import com.odms.delivery.dto.response.internal.UserResponse;
import com.odms.delivery.exception.AppException;
import com.odms.delivery.exception.ErrorCode;
import com.odms.delivery.repository.DeliveryOrderRepository;
import com.odms.delivery.service.IDeliveryOrderService;
import com.odms.delivery.utils.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryOrderServiceImpl implements IDeliveryOrderService {
    private final DeliveryOrderRepository deliveryOrderRepository;
    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${server.host_order_service}")
    private String ORDER_SERVICE_HOST;

    @Value("${server.port_order_service}")
    private String ORDER_SERVICE_PORT;

    @Value("${server.host_auth_service}")
    private String AUTH_SERVICE_HOST;

    @Value("${server.port_auth_service}")
    private String AUTH_SERVICE_PORT;

    @Value("${jwt.x-internal-token}")
    private String X_INTERNAL_TOKEN;

    @Value("${frontend.url}")
    private String FRONTEND_URL;

    @Override
    public void processOrderCreation(OrderCreateEvent orderCreateEvent) {
        DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                .id(orderCreateEvent.getId())
                .orderCode(orderCreateEvent.getOrderCode())
                .deliveryStaffId(orderCreateEvent.getDeliveryStaffId())
                .statusHistory(List.of(
                        StatusHistory.builder()
                                .status(OrderStatus.CREATED)
                                .createdBy(orderCreateEvent.getCreatedBy())
                                .updatedAt(orderCreateEvent.getCreatedAt())
                                .build()
                ))
                .build();
        deliveryOrderRepository.save(deliveryOrder);
    }

    @Override
    @SneakyThrows
    public IDResponse<String> updateDeliveryOrderStatus(UpdateDeliveryStatusRequest request) {
        List<String> roles = WebUtils.getRoles();
        if(!roles.contains("CUSTOMER") && !roles.contains("ADMIN") && !roles.contains("DELIVERY_STAFF")) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        if (roles.contains("CUSTOMER")) {
            if(request.getStatus() != OrderStatus.CANCELLED){
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }
            Integer userId = WebUtils.getCurrentUserId();
            String orderCode = request.getOrderCode();

            // check if the customerId matches the orderCode
            Boolean isMatch = this.checkCustomerIdMatchOrderCode(userId, orderCode);
            if(Boolean.FALSE.equals(isMatch)) {
                throw new AppException(ErrorCode.ORDER_NOT_FOUND);
            }

            DeliveryOrder deliveryOrder = deliveryOrderRepository.findByOrderCode(orderCode)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
            OrderStatus currentStatus = getOrderStatusCurrent(deliveryOrder.getStatusHistory());

            if(Objects.equals(currentStatus.getOrder(), OrderStatus.CANCELLED.getOrder())){
                throw new AppException(ErrorCode.ORDER_ALREADY_CANCELLED);
            }
            if(currentStatus.getOrder() >= OrderStatus.ASSIGNED.getOrder()){
                throw new AppException(ErrorCode.ORDER_ALREADY_ASSIGNED);
            }

            deliveryOrder.getStatusHistory().add(
                    StatusHistory.builder()
                            .status(request.getStatus())
                            .createdBy(userId)
                            .updatedAt(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime())
                            .reasonCancel(ReasonCancel.CUSTOMER_CANCEL_BEFORE_ASSIGN)
                            .build()
            );
            deliveryOrderRepository.save(deliveryOrder);

            // send to tracking service
            String fullName = WebUtils.getCurrentFullName();
            this.sendMessageUpdateToTracking(orderCode, request.getStatus(), fullName,
                    ReasonCancel.CUSTOMER_CANCEL_BEFORE_ASSIGN, null, null);

            // send order-service to update delivery status
            this.sendMessageUpdateToOrderService(request.getOrderCode(), request.getStatus(), null);
        }

        if(roles.contains("ADMIN")) {
            if(request.getStatus() != OrderStatus.ASSIGNED){
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }

            DeliveryOrder deliveryOrder = deliveryOrderRepository.findByOrderCode(request.getOrderCode())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            OrderStatus currentStatus = this.getOrderStatusCurrent(deliveryOrder.getStatusHistory());

            if(currentStatus.getOrder() >= OrderStatus.ASSIGNED.getOrder()){
                throw new AppException(ErrorCode.ORDER_ALREADY_ASSIGNED);
            }

            // get customerId
            OrderResponse orderInfo = this.getOrderInfo(request.getOrderCode());
            Integer customerId = Objects.requireNonNull(orderInfo).getCustomerId();
            Integer deliveryStaffId = request.getDeliveryStaffId();

            // get email of customer and delivery staff
            Map<Integer, UserResponse> data = this.getUserInfo(List.of(customerId, deliveryStaffId));
            String emailCustomer = data.get(customerId).getEmail();
            String emailDeliveryStaff = data.get(deliveryStaffId).getEmail();
            String fullNameDeliveryStaff = data.get(deliveryStaffId).getFullName();

            // Call to update delivery staff status finding order
            Integer dsId = this.updateFindOrderStatus(deliveryStaffId);

            // save delivery order status
            deliveryOrder.getStatusHistory().add(
                    StatusHistory.builder()
                            .status(request.getStatus())
                            .createdBy(WebUtils.getCurrentUserId())
                            .updatedAt(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime())
                            .build()
            );
            deliveryOrder.setDeliveryStaffId(request.getDeliveryStaffId());
            deliveryOrderRepository.save(deliveryOrder);

            // send email to customer
            NotificationEvent notificationEventCustomer = NotificationEvent.builder()
                    .typeMail(TypeMail.ASSIGNED_CUSTOMER)
                    .recipient(emailCustomer)
                    .content("Mã đơn hàng: " + request.getOrderCode() +
                            "\nMô tả đơn hàng: " + orderInfo.getDescription() +
                            "\nTên người nhận: " + orderInfo.getReceiverName() +
                            "\nĐịa chỉ nhận hàng: " + orderInfo.getDeliveryAddress() +
                            "\nSố điện thoại người nhận: " + orderInfo.getReceiverPhone() +
                            "\nNhân viên giao hàng: " + fullNameDeliveryStaff +
                            "\nClick để xem chi tiết: " + FRONTEND_URL + "/order/detail/" + request.getOrderCode())
                    .build();
            String notificationJsonCustomer = objectMapper.writeValueAsString(notificationEventCustomer);
            kafkaTemplate.send("notification-topic", notificationJsonCustomer);

            // send email to delivery staff
            NotificationEvent notificationEventDS = NotificationEvent.builder()
                    .typeMail(TypeMail.ASSIGNED_DELIVERY)
                    .recipient(emailDeliveryStaff)
                    .content("Mã đơn hàng: " + request.getOrderCode() +
                            "\nMô tả đơn hàng: " + orderInfo.getDescription() +
                            "\nĐịa chỉ lấy hàng: " + orderInfo.getPickupAddress() +
                            "\nTên người nhận: " + orderInfo.getReceiverName() +
                            "\nĐịa chỉ nhận hàng: " + orderInfo.getDeliveryAddress() +
                            "\nSố điện thoại người nhận: " + orderInfo.getReceiverPhone() +
                            "\nClick để xem chi tiết: " + FRONTEND_URL + "/order/detail/" + request.getOrderCode())
                    .build();
            String notificationJsonDS = objectMapper.writeValueAsString(notificationEventDS);
            kafkaTemplate.send("notification-topic", notificationJsonDS);

            // send to tracking service
            this.sendMessageUpdateToTracking(request.getOrderCode(), request.getStatus(), WebUtils.getCurrentFullName(),
                    null, null, deliveryStaffId);

            // send order-service to update delivery status
            this.sendMessageUpdateToOrderService(request.getOrderCode(), request.getStatus(), request.getDeliveryStaffId());

        }

        if(roles.contains("DELIVERY_STAFF")) {
            if(request.getStatus() == OrderStatus.CREATED || request.getStatus() == OrderStatus.ASSIGNED){
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }
            if(request.getStatus() == OrderStatus.CANCELLED &&
                    (request.getReasonCancel() == null || request.getReasonCancel() == ReasonCancel.CUSTOMER_CANCEL_BEFORE_ASSIGN)) {
                throw new AppException(ErrorCode.REASON_CANCEL_REQUIRED);
            }

            Integer userId = WebUtils.getCurrentUserId();
            DeliveryOrder deliveryOrder = deliveryOrderRepository.findByOrderCode(request.getOrderCode())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
            if(!Objects.equals(deliveryOrder.getDeliveryStaffId(), userId)) {
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }

            OrderStatus currentStatus = getOrderStatusCurrent(deliveryOrder.getStatusHistory());
            if(currentStatus.getOrder() >= request.getStatus().getOrder()) {
                throw new AppException(ErrorCode.ORDER_STATUS_NOT_ALLOW);
            }

            // get customerId
            OrderResponse orderInfo = this.getOrderInfo(request.getOrderCode());
            Integer customerId = Objects.requireNonNull(orderInfo).getCustomerId();

            // get email of customer and info delivery staff
            Map<Integer, UserResponse> data = this.getUserInfo(List.of(customerId, userId));
            String emailCustomer = data.get(customerId).getEmail();
            String phoneDeliveryStaff = data.get(userId).getPhone();
            String fullNameDeliveryStaff = data.get(userId).getFullName();

            // save delivery order
            StatusHistory statusHistory = StatusHistory.builder()
                    .status(request.getStatus())
                    .createdBy(userId)
                    .updatedAt(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime())
                    .build();
            if (request.getStatus() == OrderStatus.CANCELLED) {
                statusHistory.setReasonCancel(request.getReasonCancel());
                statusHistory.setNoteCancel(request.getNoteCancel());
            }
            deliveryOrder.getStatusHistory().add(statusHistory);
            deliveryOrderRepository.save(deliveryOrder);

            // send notification to customer
            if(request.getStatus() == OrderStatus.CANCELLED) {
                NotificationEvent notificationEventCancel = NotificationEvent.builder()
                        .typeMail(TypeMail.CANCELLED)
                        .recipient(emailCustomer)
                        .content("Mã đơn hàng: " + request.getOrderCode() +
                                "\nMô tả đơn hàng: " + orderInfo.getDescription() +
                                "\nSố điện thoại người nhận: " + orderInfo.getReceiverPhone() +
                                "\nNhân viên giao hàng: " + fullNameDeliveryStaff +
                                "\nSố điện thoại nhân viên giao hàng: " + phoneDeliveryStaff +
                                "\nLý do hủy: " + request.getReasonCancel().getDescription() + (request.getNoteCancel()!=null ? " - " + request.getNoteCancel(): "") +
                                "\nClick để xem chi tiết: " + FRONTEND_URL + "/order/detail/" + request.getOrderCode())
                        .build();
                String notificationJsonCustomer = objectMapper.writeValueAsString(notificationEventCancel);
                kafkaTemplate.send("notification-topic", notificationJsonCustomer);

            }
            if(request.getStatus() == OrderStatus.COMPLETED) {
                NotificationEvent notificationEventComplete = NotificationEvent.builder()
                        .typeMail(TypeMail.COMPLETED)
                        .recipient(emailCustomer)
                        .content("Mã đơn hàng: " + request.getOrderCode() +
                                "\nMô tả đơn hàng: " + orderInfo.getDescription() +
                                "\nTên người nhận: " + orderInfo.getReceiverName() +
                                "\nĐịa chỉ nhận hàng: " + orderInfo.getDeliveryAddress() +
                                "\nClick để xem chi tiết: " + FRONTEND_URL + "/order/detail/" + request.getOrderCode())
                        .build();
                String notificationJsonCustomer = objectMapper.writeValueAsString(notificationEventComplete);
                kafkaTemplate.send("notification-topic", notificationJsonCustomer);
            }

            // send to tracking service
            this.sendMessageUpdateToTracking(request.getOrderCode(), request.getStatus(), fullNameDeliveryStaff,
                    request.getReasonCancel(), request.getNoteCancel(), null);

            // send order-service to update delivery status
            this.sendMessageUpdateToOrderService(request.getOrderCode(), request.getStatus(), null);
        }

        return IDResponse.<String>builder()
                .id(request.getOrderCode())
                .build();
    }

    @Override
    public DeliveryInfo getDeliveryOrderStatusHistory(String orderCode) {
        DeliveryOrder deliveryOrder = deliveryOrderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        List<StatusHistory> statusHistories = deliveryOrder
                .getStatusHistory()
                .stream()
                .sorted(Comparator.comparing(StatusHistory::getUpdatedAt))
                .toList();

        List<Integer> userIds = statusHistories.stream()
                .map(StatusHistory::getCreatedBy)
                .distinct()
                .toList();
        Map<Integer, UserResponse> userInfo = this.getUserInfo(userIds);

        List<com.odms.delivery.dto.event.StatusHistory> status = statusHistories.stream()
                .map(statusHistory -> com.odms.delivery.dto.event.StatusHistory.builder()
                        .status(statusHistory.getStatus().getDescription())
                        .createdBy(userInfo.get(statusHistory.getCreatedBy()).getFullName()) // can error null if user not found
                        .updatedAt(statusHistory.getUpdatedAt())
                        .reasonCancel(statusHistory.getReasonCancel() != null ? statusHistory.getReasonCancel().getDescription() : null)
                        .noteCancel(statusHistory.getNoteCancel())
                        .build())
                .toList();
        Integer deliveryStaffId = deliveryOrder.getDeliveryStaffId();

        return DeliveryInfo.builder()
                .deliveryStaffId(deliveryStaffId)
                .statusHistory(status)
                .build();
    }

    private OrderStatus getOrderStatusCurrent(List<StatusHistory> statusHistories) {
        return statusHistories.stream()
                .map(StatusHistory::getStatus)
                .max(Comparator.comparingInt(OrderStatus::getOrder))
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
    }

    // call order-service
    private OrderResponse getOrderInfo(String orderCode) {
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-Token", X_INTERNAL_TOKEN);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ParameterizedTypeReference<Response<OrderResponse>> typeRef =
                    new ParameterizedTypeReference<>() {};
            ResponseEntity<Response<OrderResponse>> response= restTemplate.exchange(
                    "http://" + ORDER_SERVICE_HOST + ":" + ORDER_SERVICE_PORT +
                            "/order/order/internal/order/{orderCode}",
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

    // call auth-service to update delivery staff status finding order
    private Integer updateFindOrderStatus(Integer deliveryStaffId) {
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-Token", X_INTERNAL_TOKEN);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ParameterizedTypeReference<Response<IDResponse<Integer>>> typeRef =
                    new ParameterizedTypeReference<>() {};
            ResponseEntity<Response<IDResponse<Integer>>> response = restTemplate.exchange(
                    "http://" + AUTH_SERVICE_HOST + ":" + AUTH_SERVICE_PORT +
                            "/auth/delivery-staff/internal/update/status-finding-order/{userId}",
                    HttpMethod.GET,
                    entity,
                    typeRef,
                    deliveryStaffId
            );
            return Objects.requireNonNull(response.getBody()).getData().getId();
        } catch (Exception exception) {
            this.throwException(exception.getMessage());
            return null;
        }
    }

    // call order-service to check if customerId matches orderCode
    private Boolean checkCustomerIdMatchOrderCode(Integer customerId, String orderCode) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-Token", X_INTERNAL_TOKEN);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ParameterizedTypeReference<Response<Boolean>> typeRef =
                    new ParameterizedTypeReference<>() {};
            ResponseEntity<Response<Boolean>> response = restTemplate.exchange(
                    "http://" + ORDER_SERVICE_HOST + ":" + ORDER_SERVICE_PORT +
                            "/order/order/internal/check-customer-id/{customerId}/{orderCode}",
                    HttpMethod.GET,
                    entity,
                    typeRef,
                    customerId, orderCode
            );
            return Objects.requireNonNull(response.getBody()).getData();
        } catch (Exception exception){
            this.throwException(exception.getMessage());
            return null;
        }
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

    private void sendMessageUpdateToTracking(String orderCode,
                                             OrderStatus status,
                                             String fullName,
                                             ReasonCancel reasonCancel,
                                             String noteCancel,
                                             Integer deliveryStaffId) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        UpdateDeliveryStatusEvent updateDeliveryStatusEvent = UpdateDeliveryStatusEvent.builder()
                .orderCode(orderCode)
                .deliveryStaffId(deliveryStaffId)
                .statusHistory(com.odms.delivery.dto.event.StatusHistory.builder()
                        .status(status.getDescription())
                        .createdBy(fullName)
                        .updatedAt(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime())
                        .reasonCancel(reasonCancel != null ? reasonCancel.getDescription() : null)
                        .noteCancel(noteCancel)
                        .build())
                .build();
        String updateDeliveryStatusEventJson = objectMapper.writeValueAsString(updateDeliveryStatusEvent);
        kafkaTemplate.send("update-delivery-status-tracking-topic", updateDeliveryStatusEventJson);
    }

    private void sendMessageUpdateToOrderService(String orderCode, OrderStatus status, Integer deliveryStaffId) throws Exception {
        UpdateDeliveryStatusToOrderEvent updateStatusToOrderEvent = UpdateDeliveryStatusToOrderEvent.builder()
                .orderCode(orderCode)
                .status(status)
                .deliveryStaffId(deliveryStaffId)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String updateStatusDelivery = objectMapper.writeValueAsString(updateStatusToOrderEvent);
        kafkaTemplate.send("update-delivery-status-order-topic", updateStatusDelivery);
    }
}
