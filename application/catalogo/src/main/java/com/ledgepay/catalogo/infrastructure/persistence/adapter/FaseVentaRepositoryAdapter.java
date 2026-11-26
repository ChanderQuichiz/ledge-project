package com.ledgepay.catalogo.infrastructure.persistence.adapter;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ledgepay.catalogo.domain.FaseVenta;
import com.ledgepay.catalogo.domain.exceptions.EventoNotFoundException;
import com.ledgepay.catalogo.domain.repositories.FaseVentaRepository;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.mapper.PersistenceMapper;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.repository.FaseVentaJpaRepository;

@Component
public class FaseVentaRepositoryAdapter implements FaseVentaRepository {

    private final FaseVentaJpaRepository faseVentaJpaRepository;

    public FaseVentaRepositoryAdapter(FaseVentaJpaRepository faseVentaJpaRepository) {
        this.faseVentaJpaRepository = faseVentaJpaRepository;
    }

    @Override
    public FaseVenta findByEventoId(UUID eventoId) {
        return faseVentaJpaRepository.findByEventoId(eventoId)
            .map(PersistenceMapper::toDomain)
            .orElseThrow(() -> new EventoNotFoundException("No se encontró la fase de venta del evento: " + eventoId));
    }

    @Override
    public void save(FaseVenta faseVenta) {
        faseVentaJpaRepository.save(PersistenceMapper.toEntity(faseVenta));
    }
}
