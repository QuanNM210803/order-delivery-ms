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
@Table(name = "weight_range")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightRange extends BaseEntity {
    @NotNull
    @Column(name = "from_gam", nullable = false)
    private Double fromGam;

    @Column(name = "to_gam")
    private Double toGam;

    @NotNull
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

}