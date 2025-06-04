package com.odms.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_staff")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryStaff extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_staff_id")
    private Integer deliveryStaffId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "finding_order", nullable = false)
    private Boolean findingOrder;
}
