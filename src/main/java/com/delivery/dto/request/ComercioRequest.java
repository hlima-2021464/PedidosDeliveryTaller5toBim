package com.delivery.dto.request;

import com.delivery.entity.CategoriaComercio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComercioRequest {
    @NotBlank(message = "El nombre del comercio es obligatorio")
    private String nombre;

    @NotNull(message = "La categoria es obligatoria")
    private CategoriaComercio categoria;

    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;
}