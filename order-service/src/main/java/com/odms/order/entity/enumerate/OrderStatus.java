package com.odms.order.entity.enumerate;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED(1),
    ASSIGNED(2),
    PICKED_UP(3),
    IN_TRANSIT(4),
    DELIVERED(5),
    COMPLETED(6),
    CANCELLED(7)
    ;

    OrderStatus(Integer order) {
        this.order = order;
    }
    private Integer order;
}

