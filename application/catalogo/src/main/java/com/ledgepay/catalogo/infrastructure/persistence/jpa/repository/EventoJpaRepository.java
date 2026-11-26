package com.ledgepay.catalogo.infrastructure.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ledgepay.catalogo.infrastructure.persistence.jpa.entity.EventoEntity;

public interface EventoJpaRepository extends JpaRepository<EventoEntity, UUID> {

    Optional<EventoEntity> findBySedeId(UUID sedeId);
}
