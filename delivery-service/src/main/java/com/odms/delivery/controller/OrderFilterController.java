package com.odms.delivery.controller;

import com.odms.delivery.dto.response.FilterResponse;
import com.odms.delivery.dto.response.OrderFilterResponse;
import com.odms.delivery.dto.response.Response;
import com.odms.delivery.service.IOrderFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-filter")
@RequiredArgsConstructor
public class OrderFilterController {

    private final IOrderFilterService orderFilterService;

//    @GetMapping("/customer")
//    public ResponseEntity<Response<FilterResponse<OrderFilterResponse>>>

}
