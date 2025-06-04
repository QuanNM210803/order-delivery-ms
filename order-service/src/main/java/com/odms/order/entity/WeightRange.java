package com.odms.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "weight_ranges")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WeightRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "from_gam", nullable = false)
    private Double fromGam;

    @Column(name = "to_gam")
    private Double toGam;
}
