package com.delivery.controller;

import com.delivery.dto.request.ComercioRequest;
import com.delivery.dto.request.ProductoRequest;
import com.delivery.entity.CategoriaComercio;
import com.delivery.entity.Comercio;
import com.delivery.entity.Producto;
import com.delivery.service.ComercioService;
import com.delivery.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comercios")
public class ComercioController {

    private final ComercioService comercioService;
    private final ProductoService productoService;

    public ComercioController(ComercioService comercioService, ProductoService productoService) {
        this.comercioService = comercioService;
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<Comercio>> listarComercios(@RequestParam(required = false) CategoriaComercio categoria) {
        return ResponseEntity.ok(comercioService.listarComercios(categoria));
    }

    @PostMapping
    public ResponseEntity<Comercio> registrarComercio(@RequestBody ComercioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comercioService.registrarComercio(request));
    }

    @GetMapping("/{id}/productos")
    public ResponseEntity<List<Producto>> listarProductosPorComercio(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.listarProductosPorComercio(id));
    }

    @PostMapping("/{id}/productos")
    public ResponseEntity<Producto> agregarProducto(@PathVariable Long id, @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.agregarProducto(id, request));
    }
}