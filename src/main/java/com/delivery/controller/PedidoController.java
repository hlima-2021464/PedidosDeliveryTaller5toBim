package com.delivery.controller;

import com.delivery.dto.request.ActualizarEstadoRequest;
import com.delivery.dto.request.CrearPedidoRequest;
import com.delivery.dto.response.PedidoResponse;
import com.delivery.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CrearPedidoRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.crearPedido(userDetails.getUsername(), request));
    }

    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<PedidoResponse>> listarMisPedidos(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(pedidoService.listarMisPedidos(userDetails.getUsername()));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<PedidoResponse>> listarPedidosDisponibles() {
        return ResponseEntity.ok(pedidoService.listarPedidosDisponibles());
    }

    @PATCH
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> actualizarEstado(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ActualizarEstadoRequest request
    ) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, request.getEstado(), userDetails.getUsername()));
    }

    @PATCH
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelarPedido(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id, userDetails.getUsername()));
    }
}