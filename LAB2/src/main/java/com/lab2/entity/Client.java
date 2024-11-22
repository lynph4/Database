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
@Table(name = "\"Client\"")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public final class Client {

    @Id
    @Column(name = "\"Email\"", nullable = false, unique = true, length = 32)
    private String email;

    @Column(name = "\"Name\"", nullable = false, length = 255)
    private String name;

    @Column(name = "\"Phone\"", nullable = false, length = 10)
    private String phone;
}