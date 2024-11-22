package com.lab2.common;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@NoArgsConstructor
@AllArgsConstructor
@Data
public final class ClientFilterParameters {
    private String orderStartDate;
    private int maxMealPrice;
    private String email;
}
