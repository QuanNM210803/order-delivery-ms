package com.odms.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import nmquan.commonlib.model.BaseEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "shipping_fee")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFee extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "distance_range_id", nullable = false)
    private DistanceRange distanceRange;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "weight_range_id", nullable = false)
    private com.odms.order.entity.WeightRange weightRange;

    @NotNull
    @Column(name = "from_price", nullable = false)
    private Double fromPrice;

    @Column(name = "to_price")
    private Double toPrice;

}