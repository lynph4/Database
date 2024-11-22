package com.lab2.common;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@NoArgsConstructor
@AllArgsConstructor
@Data
public final class ClientAnalytics {
    private String name;
    private long orderCount;
    private long totalSpent;
}