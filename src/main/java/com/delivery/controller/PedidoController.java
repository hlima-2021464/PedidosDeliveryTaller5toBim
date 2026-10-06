package com.delivery.controller;

import com.delivery.dto.request.ActualizarEstadoRequest;
import com.delivery.dto.request.CrearPedidoRequest;
import com.delivery.dto.response.PedidoResponse;
import com.delivery.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(@RequestBody CrearPedidoRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedido(request, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarPedidos(Principal principal) {
        return ResponseEntity.ok(pedidoService.listarPedidos(principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPedidoPorId(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorId(id, principal.getName()));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestBody ActualizarEstadoRequest request,
            Principal principal) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, request.getEstado(), principal.getName()));
    }
}