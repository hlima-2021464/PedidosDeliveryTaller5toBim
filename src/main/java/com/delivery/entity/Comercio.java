package com.delivery.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comercios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comercio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaComercio categoria;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false)
    private Boolean abierto = true;
}