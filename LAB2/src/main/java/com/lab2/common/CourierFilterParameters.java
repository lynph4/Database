package com.lab2.common;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@NoArgsConstructor
@AllArgsConstructor
@Data
public final class CourierFilterParameters {
    private String startDeliveryDate;
    private int minRating;
}
