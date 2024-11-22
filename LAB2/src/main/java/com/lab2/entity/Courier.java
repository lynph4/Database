package com.lab2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "\"Courier\"")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public final class Courier {

    @Id
    @Column(name = "\"Phone\"", nullable = false, unique = true, length = 10)
    private String phone;

    @Column(name = "\"Name\"", nullable = false, length = 25)
    private String name;

    @Column(name = "\"Transport\"", nullable = false, length = 25)
    private String transport;
}