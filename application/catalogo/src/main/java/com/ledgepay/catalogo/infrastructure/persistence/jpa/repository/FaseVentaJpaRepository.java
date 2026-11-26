package com.ledgepay.catalogo.infrastructure.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ledgepay.catalogo.infrastructure.persistence.jpa.entity.FaseVentaEntity;

public interface FaseVentaJpaRepository extends JpaRepository<FaseVentaEntity, UUID> {

    Optional<FaseVentaEntity> findByEventoId(UUID eventoId);
}
