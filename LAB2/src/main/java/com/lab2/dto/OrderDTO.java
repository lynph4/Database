package com.lab2.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public final class OrderDTO {
    private long orderID;
    private String orderDate;
    private String courierPhone;
    private String deliveryDate; 
    private String clientEmail;
    private int rating;
    private String deliveryAddress;
}