package com.ledgepay.catalogo.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ledgepay.catalogo.infrastructure.persistence.jpa.entity.ZonaSedeEntity;

public interface ZonaSedeJpaRepository extends JpaRepository<ZonaSedeEntity, UUID> {

    List<ZonaSedeEntity> findBySedeId(UUID sedeId);
}
