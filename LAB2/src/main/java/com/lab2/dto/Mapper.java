package com.lab2.dto;

import java.time.format.DateTimeFormatter;

import com.lab2.entity.*;

public final class Mapper {
    public ClientDTO toDto(Client client) {
        return ClientDTO.builder()
            .email(client.getEmail())
            .name(client.getName())
            .phone(client.getPhone())
            .build();
    }

    public CourierDTO toDto(Courier courier) {
        return CourierDTO.builder()
            .phone(courier.getPhone())
            .name(courier.getName())
            .transport(courier.getTransport())
            .build();
    }

    public MealDTO toDto(Meal meal) {
        return MealDTO.builder()
            .mealID(meal.getMealID().intValue())
            .orderID(meal.getOrder().getOrderID().intValue())
            .name(meal.getName())
            .price(meal.getPrice())
            .weight(meal.getPrice())
            .servingSize(meal.getServingSize())
            .build();
    }

    public OrderDTO toDto(Order order) {
        return OrderDTO.builder()
            .orderID(order.getOrderID().intValue())
            .orderDate(order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
            .courierPhone(order.getCourier().getPhone())
            .deliveryDate(order.getDeliveryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
            .clientEmail(order.getClient().getEmail())
            .rating(order.getRating())
            .deliveryAddress(order.getDeliveryAddress())
            .build();
    }
}
