package com.delivery.dto.request;

import com.delivery.entity.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoRequest {
    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoPedido estado;
}