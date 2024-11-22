package com.lab2.common;

import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@NoArgsConstructor
@AllArgsConstructor
@Data
public final class CourierAnalytics {
    private String name;
    private String phone;
    private Double averageRating;
    private LocalDateTime lastDeliveryDate;
    private LocalDateTime firstOrderDate;
}