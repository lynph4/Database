package com.lab2.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public final class CourierDTO {
    private String phone; 
    private String name;
    private String transport;
}