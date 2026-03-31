package com.tablemint.backend.entity;

import com.tablemint.backend.enums.OperatingDay;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "operating_hours")
@Data
@NoArgsConstructor
public class OperatingHours {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    private OperatingDay day;

    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean closed = false;
}