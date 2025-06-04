package com.odms.order.service.impl;

import com.odms.order.dto.request.OrderRequest;
import com.odms.order.dto.response.IDResponse;
import com.odms.order.entity.Order;
import com.odms.order.entity.enumerate.OrderStatus;
import com.odms.order.repository.OrderRepository;
import com.odms.order.service.IOrderService;
import com.odms.order.service.IShippingFeeService;
import com.odms.order.utils.WebUtils;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    @Value("${api-key.open-route-service}")
    private String OPEN_ROUTE_SERVICE_API_KEY;

    private final RestTemplate restTemplate = new RestTemplate();
    private final IShippingFeeService shippingFeeService;
    private final OrderRepository orderRepository;

    @Override
    public IDResponse<String> createOrder(OrderRequest orderRequest) {
        String orderCode = this.generateOrderCode();
        Integer customerId = WebUtils.getCurrentUserId();
        Double weight = orderRequest.getWeight()*1000; // Convert kg to grams
        OrderStatus statusCreated = OrderStatus.CREATED;
        Double distance = this.getDistance(orderRequest.getPickupAddress(), orderRequest.getDeliveryAddress());
        Double shippingFee = this.shippingFeeService.calculateShippingFee(distance, weight);

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
                .orderStatus(statusCreated)
                .distance(distance)
                .shippingFee(shippingFee)
                .build();
        orderRepository.save(order);

        // SEND KAFKA

        return IDResponse.<String>builder()
                .id(order.getOrderCode())
                .build();
    }

    private Double getDistance(String location1, String location2) {
        try {
            JSONObject coordinates1 = getCoordinates(location1);
            double lon1 = coordinates1.getDouble("lon");
            double lat1 = coordinates1.getDouble("lat");

            JSONObject coordinates2 = getCoordinates(location2);
            double lon2 = coordinates2.getDouble("lon");
            double lat2 = coordinates2.getDouble("lat");

            String routeUrl = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=" + OPEN_ROUTE_SERVICE_API_KEY + "&start=" + lon1 + "," + lat1 + "&end=" + lon2 + "," + lat2;
            JSONObject routeResponse = new JSONObject(restTemplate.getForObject(routeUrl, String.class));
            JSONObject routes = routeResponse
                    .getJSONArray("features")
                    .getJSONObject(0)
                    .getJSONObject("properties")
                    .getJSONObject("summary");

            return routes.getDouble("distance");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject getCoordinates(String location) {
        try {
            String url = "https://nominatim.openstreetmap.org/search?q=" + location + ", Ha Noi, Viet Nam&format=json&limit=1";
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JSONArray response = new JSONArray(jsonResponse);
            return response.getJSONObject(0);
        } catch (JSONException e) {
            String[] parts = location.split(",", 2);
            if (parts.length >= 2) {
                location = parts[1].trim();
            }
            String url = "https://nominatim.openstreetmap.org/search?q=" + location + ", Ha Noi, Viet Nam&format=json&limit=1";
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JSONArray response = new JSONArray(jsonResponse);
            return response.getJSONObject(0);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
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
