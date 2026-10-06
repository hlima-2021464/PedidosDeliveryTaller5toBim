package com.delivery.repository;

import com.delivery.entity.CategoriaComercio;
import com.delivery.entity.Comercio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComercioRepository extends JpaRepository<Comercio, Long> {
    List<Comercio> findByAbiertoTrue();
    List<Comercio> findByAbiertoTrueAndCategoria(CategoriaComercio categoria);
}