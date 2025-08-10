package com.odms.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import nmquan.commonlib.model.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "distance_range")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistanceRange extends BaseEntity {
    @NotNull
    @Column(name = "from_m", nullable = false)
    private Double fromM;

    @Column(name = "to_m")
    private Double toM;

    @NotNull
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

}