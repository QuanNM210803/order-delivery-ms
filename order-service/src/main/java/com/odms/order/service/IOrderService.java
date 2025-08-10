package com.odms.order.service;

import com.odms.order.dto.event.UpdateDeliveryStatusEvent;
import com.odms.order.dto.request.FilterOrderAdmin;
import com.odms.order.dto.request.FilterOrderCustomer;
import com.odms.order.dto.request.FilterOrderDelivery;
import com.odms.order.dto.request.OrderRequest;
import com.odms.order.dto.response.OrderFilterResponse;
import com.odms.order.dto.response.OrderResponse;
import nmquan.commonlib.dto.response.FilterResponse;
import nmquan.commonlib.dto.response.IDResponse;

public interface IOrderService {
    IDResponse<String> createOrder(OrderRequest orderRequest);
    boolean checkCustomerId(Long customerId, String orderCode);
    OrderResponse getOrderByOrderCode(String orderCode);
    void updateStatusDelivery(UpdateDeliveryStatusEvent request);

    FilterResponse<OrderFilterResponse> filterByCustomer(FilterOrderCustomer request);
    FilterResponse<OrderFilterResponse> filterByDelivery(FilterOrderDelivery request);
    FilterResponse<OrderFilterResponse> filterByAdmin(FilterOrderAdmin request);
}
