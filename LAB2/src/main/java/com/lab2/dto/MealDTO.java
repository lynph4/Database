package com.lab2.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public final class MealDTO {
    private long mealID;
    private long orderID;
    private String name; 
    private int price;
    private int weight; 
    private int servingSize;
}