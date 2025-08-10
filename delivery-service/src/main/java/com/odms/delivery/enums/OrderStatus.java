package com.odms.delivery.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED(1, "Tạo đơn"),
    ASSIGNED(2, "Đã giao cho nhân viên"),
    PICKED_UP(3, "Đã lấy hàng"),
    IN_TRANSIT(4, "Đang vận chuyển"),
    DELIVERED(5, "Đã giao hàng"),
    COMPLETED(6, "Hoàn thành"),
    CANCELLED(6, "Đã hủy")
    ;

    OrderStatus(Integer order, String description) {
        this.order = order;
        this.description = description;
    }
    private Integer order;
    private String description;
}

