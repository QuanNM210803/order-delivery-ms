package com.odms.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipping_fees", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"distance_range_id", "weight_range_id"})
})
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ShippingFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "distance_range_id", nullable = false)
    private DistanceRange distanceRange;

    @ManyToOne
    @JoinColumn(name = "weight_range_id", nullable = false)
    private WeightRange weightRange;

    @Column(name = "from_price", nullable = false)
    private Double fromPrice;

    @Column(name = "to_price")
    private Double toPrice;
}
