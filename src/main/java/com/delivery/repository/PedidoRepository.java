package com.delivery.repository;

import com.delivery.entity.EstadoPedido;
import com.delivery.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteIdOrderByFechaPedidoDesc(Long clienteId);
    List<Pedido> findByEstadoInOrderByFechaPedidoDesc(List<EstadoPedido> estados);
}