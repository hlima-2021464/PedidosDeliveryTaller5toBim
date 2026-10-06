package com.delivery.dto.response;

import com.delivery.entity.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedidoResponse {
    private Long id;
    private String clienteEmail;
    private String repartidorEmail;
    private LocalDateTime fechaPedido;
    private BigDecimal costoEnvio;
    private BigDecimal montoTotal;
    private EstadoPedido estado;
    private List<DetallePedidoResponse> detalles;
}