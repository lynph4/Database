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
import java.time.LocalDateTime;

@Entity
@Table(name = "\"Order\"")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public final class Order {

    @Id
    @Column(name = "\"Order ID\"")
    private Long orderID;

    @ManyToOne
    @JoinColumn(name = "\"Courier Phone\"", referencedColumnName = "\"Phone\"", nullable = false)
    private Courier courier;
    
    @ManyToOne
    @JoinColumn(name = "\"Client Email\"", referencedColumnName = "\"Email\"", nullable = false)
    private Client client;

    @Column(name = "\"Order Date\"", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "\"Delivery Date\"", nullable = false)
    private LocalDateTime deliveryDate;

    @Column(name = "\"Rating\"", nullable = false)
    private Integer rating;

    @Column(name = "\"Delivery Address\"", nullable = false, length = 50)
    private String deliveryAddress;
}