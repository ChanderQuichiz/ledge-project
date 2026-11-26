package com.ledgepay.catalogo.infrastructure.persistence.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ledgepay.catalogo.domain.PuntoAccesoZona;
import com.ledgepay.catalogo.domain.repositories.PuntoAccesoZonaRepository;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.mapper.PersistenceMapper;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.repository.PuntoAccesoZonaJpaRepository;

@Component
public class PuntoAccesoZonaRepositoryAdapter implements PuntoAccesoZonaRepository {

    private final PuntoAccesoZonaJpaRepository puntoAccesoZonaJpaRepository;

    public PuntoAccesoZonaRepositoryAdapter(PuntoAccesoZonaJpaRepository puntoAccesoZonaJpaRepository) {
        this.puntoAccesoZonaJpaRepository = puntoAccesoZonaJpaRepository;
    }

    @Override
    public List<PuntoAccesoZona> findByPuntoAcessoSedeId(UUID puntoAccesoId) {
        return puntoAccesoZonaJpaRepository.findByIdPuntoAccesoId(puntoAccesoId).stream()
            .map(PersistenceMapper::toDomain)
            .toList();
    }
}
