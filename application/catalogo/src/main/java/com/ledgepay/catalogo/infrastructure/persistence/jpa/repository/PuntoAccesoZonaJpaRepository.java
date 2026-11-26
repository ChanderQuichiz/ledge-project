package com.ledgepay.catalogo.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ledgepay.catalogo.infrastructure.persistence.jpa.entity.PuntoAccesoZonaEntity;

public interface PuntoAccesoZonaJpaRepository
        extends JpaRepository<PuntoAccesoZonaEntity, PuntoAccesoZonaEntity.PuntoAccesoZonaId> {

    List<PuntoAccesoZonaEntity> findByIdPuntoAccesoId(UUID puntoAccesoId);

    List<PuntoAccesoZonaEntity> findByIdZonaId(UUID zonaId);
}
