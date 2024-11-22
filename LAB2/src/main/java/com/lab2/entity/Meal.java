package com.lab2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "\"Meal\"")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public final class Meal {

    @Id
    @Column(name = "\"Meal ID\"")
    private Long mealID;

    @ManyToOne
    @JoinColumn(name = "\"Order ID\"", referencedColumnName = "\"Order ID\"", nullable = false)
    private Order order;

    @Column(name = "\"Name\"", nullable = false, length = 25)
    private String name;

    @Column(name = "\"Price\"", nullable = false)
    private Integer price;

    @Column(name = "\"Weight\"", nullable = false)
    private Integer weight;

    @Column(name = "\"Serving Size\"", nullable = false)
    private Integer servingSize;
}