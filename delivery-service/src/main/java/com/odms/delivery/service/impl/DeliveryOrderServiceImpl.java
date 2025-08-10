package com.odms.delivery.service.impl;

import com.odms.delivery.document.DeliveryOrder;
import com.odms.delivery.document.StatusHistory;
import com.odms.delivery.enums.OrderStatus;
import com.odms.delivery.enums.ReasonCancel;
import com.odms.delivery.enums.TypeMail;
import com.odms.delivery.dto.event.NotificationEvent;
import com.odms.delivery.dto.event.OrderCreateEvent;
import com.odms.delivery.dto.event.UpdateDeliveryStatusEvent;
import com.odms.delivery.dto.event.UpdateDeliveryStatusToOrderEvent;
import com.odms.delivery.dto.request.UpdateDeliveryStatusRequest;
import com.odms.delivery.dto.request.internal.IdListRequest;
import com.odms.delivery.dto.response.UpdateDeliveryStatusResponse;
import com.odms.delivery.dto.response.internal.DeliveryInfo;
import com.odms.delivery.dto.response.internal.OrderResponse;
import com.odms.delivery.dto.response.internal.UserResponse;
import com.odms.delivery.enums.DeliveryErrorCode;
import com.odms.delivery.repository.DeliveryOrderRepository;
import com.odms.delivery.service.IDeliveryOrderService;
import lombok.SneakyThrows;
import nmquan.commonlib.dto.response.IDResponse;
import nmquan.commonlib.dto.response.Response;
import nmquan.commonlib.exception.AppException;
import nmquan.commonlib.exception.CommonErrorCode;
import nmquan.commonlib.service.RestTemplateService;
import nmquan.commonlib.utils.ObjectMapperUtils;
import nmquan.commonlib.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class DeliveryOrderServiceImpl implements IDeliveryOrderService {
    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${server.host_order_service}")
    private String ORDER_SERVICE_HOST;

    @Value("${server.port_order_service}")
    private String ORDER_SERVICE_PORT;

    @Value("${server.host_auth_service}")
    private String AUTH_SERVICE_HOST;

    @Value("${server.port_auth_service}")
    private String AUTH_SERVICE_PORT;

    @Value("${frontend.url}")
    private String FRONTEND_URL;

    @Autowired
    private RestTemplateService restTemplateService;

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
    public UpdateDeliveryStatusResponse updateDeliveryOrderStatus(UpdateDeliveryStatusRequest request) {
        List<String> roles = WebUtils.getCurrentRole();
        if(!roles.contains("CUSTOMER") && !roles.contains("ADMIN") && !roles.contains("DELIVERY_STAFF")) {
            throw new AppException(CommonErrorCode.ACCESS_DENIED);
        }

        if (roles.contains("CUSTOMER")) {
            if(request.getStatus() != OrderStatus.CANCELLED){
                throw new AppException(CommonErrorCode.ACCESS_DENIED);
            }
            Long userId = WebUtils.getCurrentUserId();
            String orderCode = request.getOrderCode();

            // check if the customerId matches the orderCode
            Boolean isMatch = this.checkCustomerIdMatchOrderCode(userId, orderCode);
            if(Boolean.FALSE.equals(isMatch)) {
                throw new AppException(DeliveryErrorCode.ORDER_NOT_FOUND);
            }

            DeliveryOrder deliveryOrder = deliveryOrderRepository.findByOrderCode(orderCode)
                    .orElseThrow(() -> new AppException(DeliveryErrorCode.ORDER_NOT_FOUND));
            OrderStatus currentStatus = getOrderStatusCurrent(deliveryOrder.getStatusHistory());

            if(Objects.equals(currentStatus.getOrder(), OrderStatus.CANCELLED.getOrder())){
                throw new AppException(DeliveryErrorCode.ORDER_ALREADY_CANCELLED);
            }
            if(currentStatus.getOrder() >= OrderStatus.ASSIGNED.getOrder()){
                throw new AppException(DeliveryErrorCode.ORDER_ALREADY_ASSIGNED);
            }

            deliveryOrder.getStatusHistory().add(
                    StatusHistory.builder()
                            .status(request.getStatus())
                            .createdBy(userId)
                            .updatedAt(Instant.now())
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
                throw new AppException(CommonErrorCode.ACCESS_DENIED);
            }

            DeliveryOrder deliveryOrder = deliveryOrderRepository.findByOrderCode(request.getOrderCode())
                    .orElseThrow(() -> new AppException(DeliveryErrorCode.ORDER_NOT_FOUND));

            OrderStatus currentStatus = this.getOrderStatusCurrent(deliveryOrder.getStatusHistory());

            if(currentStatus.getOrder() >= OrderStatus.ASSIGNED.getOrder()){
                throw new AppException(DeliveryErrorCode.ORDER_ALREADY_ASSIGNED);
            }

            // get customerId
            OrderResponse orderInfo = this.getOrderInfo(request.getOrderCode());
            Long customerId = Objects.requireNonNull(orderInfo).getCustomerId();
            Long deliveryStaffId = request.getDeliveryStaffId();

            // get email of customer and delivery staff
            Map<Long, UserResponse> data = this.getUserInfo(List.of(customerId, deliveryStaffId));
            String emailCustomer = data.get(customerId).getEmail();
            String emailDeliveryStaff = data.get(deliveryStaffId).getEmail();
            String fullNameDeliveryStaff = data.get(deliveryStaffId).getFullName();

            // Call to update delivery staff status finding order
            Long dsId = this.updateFindOrderStatus(deliveryStaffId);

            // save delivery order status
            deliveryOrder.getStatusHistory().add(
                    StatusHistory.builder()
                            .status(request.getStatus())
                            .createdBy(WebUtils.getCurrentUserId())
                            .updatedAt(Instant.now())
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
                            "\nClick để xem chi tiết: " + FRONTEND_URL + "/customer/order/detail/" + request.getOrderCode())
                    .build();
            String notificationJsonCustomer = ObjectMapperUtils.convertToJson(notificationEventCustomer);
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
                            "\nClick để xem chi tiết: " + FRONTEND_URL + "/delivery/order/detail/" + request.getOrderCode())
                    .build();
            String notificationJsonDS = ObjectMapperUtils.convertToJson(notificationEventDS);
            kafkaTemplate.send("notification-topic", notificationJsonDS);

            // send socket
            Map<String, Object> dataUpdate = new HashMap<>();
            dataUpdate.put("status", false);
            dataUpdate.put("userId", dsId);
            String dataUpdateJson = ObjectMapperUtils.convertToJson(dataUpdate);
            kafkaTemplate.send("update-find-order-status-topic", dataUpdateJson);

            // send to tracking service
            this.sendMessageUpdateToTracking(request.getOrderCode(), request.getStatus(), WebUtils.getCurrentFullName(),
                    null, null, deliveryStaffId);

            // send order-service to update delivery status
            this.sendMessageUpdateToOrderService(request.getOrderCode(), request.getStatus(), request.getDeliveryStaffId());

        }

        if(roles.contains("DELIVERY_STAFF")) {
            if(request.getStatus() == OrderStatus.CREATED || request.getStatus() == OrderStatus.ASSIGNED){
                throw new AppException(CommonErrorCode.ACCESS_DENIED);
            }
            if(request.getStatus() == OrderStatus.CANCELLED &&
                    (request.getReasonCancel() == null || request.getReasonCancel() == ReasonCancel.CUSTOMER_CANCEL_BEFORE_ASSIGN)) {
                throw new AppException(DeliveryErrorCode.REASON_CANCEL_REQUIRED);
            }

            Long userId = WebUtils.getCurrentUserId();
            DeliveryOrder deliveryOrder = deliveryOrderRepository.findByOrderCode(request.getOrderCode())
                    .orElseThrow(() -> new AppException(DeliveryErrorCode.ORDER_NOT_FOUND));
            if(!Objects.equals(deliveryOrder.getDeliveryStaffId(), userId)) {
                throw new AppException(CommonErrorCode.ACCESS_DENIED);
            }

            OrderStatus currentStatus = getOrderStatusCurrent(deliveryOrder.getStatusHistory());
            if(currentStatus.getOrder() >= request.getStatus().getOrder()) {
                throw new AppException(DeliveryErrorCode.ORDER_STATUS_NOT_ALLOW);
            }

            // get customerId
            OrderResponse orderInfo = this.getOrderInfo(request.getOrderCode());
            Long customerId = Objects.requireNonNull(orderInfo).getCustomerId();

            // get email of customer and info delivery staff
            Map<Long, UserResponse> data = this.getUserInfo(List.of(customerId, userId));
            String emailCustomer = data.get(customerId).getEmail();
            String phoneDeliveryStaff = data.get(userId).getPhone();
            String fullNameDeliveryStaff = data.get(userId).getFullName();

            // save delivery order
            StatusHistory statusHistory = StatusHistory.builder()
                    .status(request.getStatus())
                    .createdBy(userId)
                    .updatedAt(Instant.now())
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
                                "\nClick để xem chi tiết: " + FRONTEND_URL + "/customer/order/detail/" + request.getOrderCode())
                        .build();
                String notificationJsonCustomer = ObjectMapperUtils.convertToJson(notificationEventCancel);
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
                                "\nClick để xem chi tiết: " + FRONTEND_URL + "/customer/order/detail/" + request.getOrderCode())
                        .build();
                String notificationJsonCustomer = ObjectMapperUtils.convertToJson(notificationEventComplete);
                kafkaTemplate.send("notification-topic", notificationJsonCustomer);
            }

            // send to tracking service
            this.sendMessageUpdateToTracking(request.getOrderCode(), request.getStatus(), fullNameDeliveryStaff,
                    request.getReasonCancel(), request.getNoteCancel(), null);

            // send order-service to update delivery status
            this.sendMessageUpdateToOrderService(request.getOrderCode(), request.getStatus(), null);
        }

        return UpdateDeliveryStatusResponse.builder()
                .orderCode(request.getOrderCode())
                .status(request.getStatus().getDescription())
                .build();
    }

    @Override
    public DeliveryInfo getDeliveryOrderStatusHistory(String orderCode) {
        DeliveryOrder deliveryOrder = deliveryOrderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new AppException(DeliveryErrorCode.ORDER_NOT_FOUND));
        List<StatusHistory> statusHistories = deliveryOrder
                .getStatusHistory()
                .stream()
                .sorted(Comparator.comparing(StatusHistory::getUpdatedAt))
                .toList();

        List<Long> userIds = statusHistories.stream()
                .map(StatusHistory::getCreatedBy)
                .distinct()
                .toList();
        Map<Long, UserResponse> userInfo = this.getUserInfo(userIds);

        List<com.odms.delivery.dto.event.StatusHistory> status = statusHistories.stream()
                .map(statusHistory -> com.odms.delivery.dto.event.StatusHistory.builder()
                        .status(statusHistory.getStatus().getDescription())
                        .createdBy(userInfo.get(statusHistory.getCreatedBy()).getFullName()) // can error null if user not found
                        .updatedAt(statusHistory.getUpdatedAt())
                        .reasonCancel(statusHistory.getReasonCancel() != null ? statusHistory.getReasonCancel().getDescription() : null)
                        .noteCancel(statusHistory.getNoteCancel())
                        .build())
                .toList();
        Long deliveryStaffId = deliveryOrder.getDeliveryStaffId();

        return DeliveryInfo.builder()
                .deliveryStaffId(deliveryStaffId)
                .statusHistory(status)
                .build();
    }

    private OrderStatus getOrderStatusCurrent(List<StatusHistory> statusHistories) {
        return statusHistories.stream()
                .map(StatusHistory::getStatus)
                .max(Comparator.comparingInt(OrderStatus::getOrder))
                .orElseThrow(() -> new AppException(DeliveryErrorCode.ORDER_NOT_FOUND));
    }

    // call order-service
    private OrderResponse getOrderInfo(String orderCode) {
        String url = "http://" + ORDER_SERVICE_HOST + ":" + ORDER_SERVICE_PORT +
                "/order/order/internal/order/{orderCode}";
        Response<OrderResponse> response = restTemplateService.getMethodRestTemplate(
                url,
                new ParameterizedTypeReference<Response<OrderResponse>>() {},
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

    // call auth-service to update delivery staff status finding order
    private Long updateFindOrderStatus(Long deliveryStaffId) {
        String url ="http://" + AUTH_SERVICE_HOST + ":" + AUTH_SERVICE_PORT +
                           "/auth/delivery-staff/internal/update/status-finding-order/{userId}";
        Response<IDResponse<Long>> response = restTemplateService.getMethodRestTemplate(
                url,
                new ParameterizedTypeReference<Response<IDResponse<Long>>>() {},
                deliveryStaffId
        );
        return response != null ? response.getData().getId() : null;
    }

    // call order-service to check if customerId matches orderCode
    private Boolean checkCustomerIdMatchOrderCode(Long customerId, String orderCode) {
        String url = "http://" + ORDER_SERVICE_HOST + ":" + ORDER_SERVICE_PORT +
                "/order/order/internal/check-customer-id/{customerId}/{orderCode}";
        Response<Boolean> response = restTemplateService.getMethodRestTemplate(
                url,
                new ParameterizedTypeReference<Response<Boolean>>() {},
                customerId, orderCode
        );
        return response != null ? response.getData() : null;
    }

    private void sendMessageUpdateToTracking(String orderCode,
                                             OrderStatus status,
                                             String fullName,
                                             ReasonCancel reasonCancel,
                                             String noteCancel,
                                             Long deliveryStaffId) {
        UpdateDeliveryStatusEvent updateDeliveryStatusEvent = UpdateDeliveryStatusEvent.builder()
                .orderCode(orderCode)
                .deliveryStaffId(deliveryStaffId)
                .statusHistory(com.odms.delivery.dto.event.StatusHistory.builder()
                        .status(status.getDescription())
                        .createdBy(fullName)
                        .updatedAt(Instant.now())
                        .reasonCancel(reasonCancel != null ? reasonCancel.getDescription() : null)
                        .noteCancel(noteCancel)
                        .build())
                .build();
        String updateDeliveryStatusEventJson = ObjectMapperUtils.convertToJson(updateDeliveryStatusEvent);
        kafkaTemplate.send("update-delivery-status-tracking-topic", updateDeliveryStatusEventJson);
    }

    private void sendMessageUpdateToOrderService(String orderCode, OrderStatus status, Long deliveryStaffId) {
        UpdateDeliveryStatusToOrderEvent updateStatusToOrderEvent = UpdateDeliveryStatusToOrderEvent.builder()
                .orderCode(orderCode)
                .status(status)
                .deliveryStaffId(deliveryStaffId)
                .build();
        String updateStatusDelivery = ObjectMapperUtils.convertToJson(updateStatusToOrderEvent);
        kafkaTemplate.send("update-delivery-status-order-topic", updateStatusDelivery);
    }
}
