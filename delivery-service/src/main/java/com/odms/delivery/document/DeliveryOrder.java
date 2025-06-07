package com.odms.delivery.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "delivery_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOrder {
    @Id
    private String id;
    private String orderCode;
    private Integer deliveryStaffId;
    private List<StatusHistory> statusHistory;
}
