package com.odms.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "distance_ranges")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DistanceRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "from_m", nullable = false)
    private Double fromM;

    @Column(name = "to_m")
    private Double toM;
}
