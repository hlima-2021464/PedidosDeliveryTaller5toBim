package com.delivery.service;

import com.delivery.dto.request.ComercioRequest;
import com.delivery.entity.CategoriaComercio;
import com.delivery.entity.Comercio;
import com.delivery.repository.ComercioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComercioService {

    private final ComercioRepository comercioRepository;

    @Transactional
    public Comercio registrarComercio(ComercioRequest request) {
        Comercio comercio = Comercio.builder()
                .nombre(request.getNombre())
                .categoria(request.getCategoria())
                .direccion(request.getDireccion())
                .abierto(true)
                .build();
        return comercioRepository.save(comercio);
    }

    @Transactional(readOnly = true)
    public List<Comercio> listarComercios(CategoriaComercio categoria) {
        if (categoria != null) {
            return comercioRepository.findByAbiertoTrueAndCategoria(categoria);
        }
        return comercioRepository.findByAbiertoTrue();
    }
}