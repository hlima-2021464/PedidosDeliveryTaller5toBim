package com.delivery.service;

import com.delivery.dto.request.ProductoRequest;
import com.delivery.entity.Comercio;
import com.delivery.entity.Producto;
import com.delivery.exception.ResourceNotFoundException;
import com.delivery.repository.ComercioRepository;
import com.delivery.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ComercioRepository comercioRepository;

    @Transactional
    public Producto agregarProducto(Long comercioId, ProductoRequest request) {
        Comercio comercio = comercioRepository.findById(comercioId)
                .orElseThrow(() -> new ResourceNotFoundException("Comercio no encontrado con id: " + comercioId));

        Producto producto = Producto.builder()
                .comercio(comercio)
                .nombre(request.getNombre())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .disponible(true)
                .build();

        return productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarProductosPorComercio(Long comercioId) {
        if (!comercioRepository.existsById(comercioId)) {
            throw new ResourceNotFoundException("Comercio no encontrado con id: " + comercioId);
        }
        return productoRepository.findByComercioIdAndDisponibleTrue(comercioId);
    }
}