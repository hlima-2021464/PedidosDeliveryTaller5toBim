package com.delivery.service;

import com.delivery.dto.request.CrearPedidoRequest;
import com.delivery.dto.request.ItemPedidoRequest;
import com.delivery.dto.response.DetallePedidoResponse;
import com.delivery.dto.response.PedidoResponse;
import com.delivery.entity.*;
import com.delivery.exception.InsufficientStockException;
import com.delivery.exception.InvalidStatusException;
import com.delivery.exception.ResourceNotFoundException;
import com.delivery.repository.PedidoRepository;
import com.delivery.repository.ProductoRepository;
import com.delivery.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private static final BigDecimal COSTO_ENVIO_FIJO = new BigDecimal("20.00");

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public PedidoResponse crearPedido(String clienteEmail, CrearPedidoRequest request) {
        Usuario cliente = usuarioRepository.findByEmail(clienteEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con email: " + clienteEmail));

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .fechaPedido(LocalDateTime.now())
                .costoEnvio(COSTO_ENVIO_FIJO)
                .estado(EstadoPedido.PENDIENTE)
                .detalles(new ArrayList<>())
                .build();

        BigDecimal subtotalAcumulado = BigDecimal.ZERO;

        for (ItemPedidoRequest item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + item.getProductoId()));

            if (!producto.getDisponible()) {
                throw new InsufficientStockException("El producto " + producto.getNombre() + " no esta disponible para compra");
            }

            if (producto.getStock() < item.getCantidad()) {
                throw new InsufficientStockException("Stock insuficiente para el producto: " + producto.getNombre()
                        + ". Disponible: " + producto.getStock() + ", Solicitado: " + item.getCantidad());
            }

            // Descontar inventario
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            BigDecimal subtotalItem = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            subtotalAcumulado = subtotalAcumulado.add(subtotalItem);

            DetallePedido detalle = DetallePedido.builder()
                    .pedido(pedido)
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .subtotal(subtotalItem)
                    .build();

            pedido.getDetalles().add(detalle);
        }

        pedido.setMontoTotal(subtotalAcumulado.add(COSTO_ENVIO_FIJO));
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        return mapearAPedidoResponse(pedidoGuardado);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarMisPedidos(String clienteEmail) {
        Usuario cliente = usuarioRepository.findByEmail(clienteEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + clienteEmail));

        return pedidoRepository.findByClienteIdOrderByFechaPedidoDesc(cliente.getId())
                .stream()
                .map(this::mapearAPedidoResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPedidosDisponibles() {
        return pedidoRepository.findByEstadoInOrderByFechaPedidoDesc(List.of(
                        EstadoPedido.PENDIENTE,
                        EstadoPedido.EN_PREPARACION,
                        EstadoPedido.EN_CAMINO
                ))
                .stream()
                .map(this::mapearAPedidoResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidoResponse actualizarEstado(Long pedidoId, EstadoPedido nuevoEstado, String usuarioEmail) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + pedidoId));

        Usuario usuario = usuarioRepository.findByEmail(usuarioEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + usuarioEmail));

        // Validacion de secuencia de estados
        validarTransicionEstado(pedido.getEstado(), nuevoEstado);

        // Si es repartidor y el pedido no tiene repartidor asignado, se le asigna
        if (usuario.getRol() == Rol.REPARTIDOR && pedido.getRepartidor() == null) {
            pedido.setRepartidor(usuario);
        }

        pedido.setEstado(nuevoEstado);
        return mapearAPedidoResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse cancelarPedido(Long pedidoId, String usuarioEmail) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + pedidoId));

        Usuario usuario = usuarioRepository.findByEmail(usuarioEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usuarioEmail));

        // Solo el cliente duenio o un ADMIN pueden cancelar
        if (usuario.getRol() == Rol.CLIENTE && !pedido.getCliente().getId().equals(usuario.getId())) {
            throw new InvalidStatusException("No tiene permisos para cancelar este pedido");
        }

        // Solo se puede cancelar si esta PENDIENTE
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new InvalidStatusException("Solo se pueden cancelar pedidos en estado PENDIENTE. Estado actual: " + pedido.getEstado());
        }

        // Restaurar inventario de cada producto
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        return mapearAPedidoResponse(pedidoRepository.save(pedido));
    }

    private void validarTransicionEstado(EstadoPedido actual, EstadoPedido nuevo) {
        if (actual == EstadoPedido.CANCELADO || actual == EstadoPedido.ENTREGADO) {
            throw new InvalidStatusException("No se puede cambiar el estado de un pedido ya finalizado (" + actual + ")");
        }

        boolean transicionValida = switch (actual) {
            case PENDIENTE -> nuevo == EstadoPedido.EN_PREPARACION;
            case EN_PREPARACION -> nuevo == EstadoPedido.EN_CAMINO;
            case EN_CAMINO -> nuevo == EstadoPedido.ENTREGADO;
            default -> false;
        };

        if (!transicionValida) {
            throw new InvalidStatusException("Transicion de estado invalida: " + actual + " -> " + nuevo);
        }
    }

    private PedidoResponse mapearAPedidoResponse(Pedido pedido) {
        List<DetallePedidoResponse> detalles = pedido.getDetalles().stream()
                .map(d -> DetallePedidoResponse.builder()
                        .productoId(d.getProducto().getId())
                        .productoNombre(d.getProducto().getNombre())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return PedidoResponse.builder()
                .id(pedido.getId())
                .clienteEmail(pedido.getCliente().getEmail())
                .repartidorEmail(pedido.getRepartidor() != null ? pedido.getRepartidor().getEmail() : null)
                .fechaPedido(pedido.getFechaPedido())
                .costoEnvio(pedido.getCostoEnvio())
                .montoTotal(pedido.getMontoTotal())
                .estado(pedido.getEstado())
                .detalles(detalles)
                .build();
    }
}