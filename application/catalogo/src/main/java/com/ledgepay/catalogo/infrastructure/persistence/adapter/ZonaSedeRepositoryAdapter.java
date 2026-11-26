package com.ledgepay.catalogo.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ledgepay.catalogo.domain.ZonaSede;
import com.ledgepay.catalogo.domain.repositories.ZonaSedeRepository;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.entity.ZonaSedeEntity;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.mapper.PersistenceMapper;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.repository.ZonaSedeJpaRepository;

@Component
public class ZonaSedeRepositoryAdapter implements ZonaSedeRepository {

    private final ZonaSedeJpaRepository zonaSedeJpaRepository;

    public ZonaSedeRepositoryAdapter(ZonaSedeJpaRepository zonaSedeJpaRepository) {
        this.zonaSedeJpaRepository = zonaSedeJpaRepository;
    }

    @Override
    public Optional<ZonaSede> findById(UUID id) {
        return zonaSedeJpaRepository.findById(id).map(PersistenceMapper::toDomain);
    }

    @Override
    public void save(ZonaSede zona) {
        ZonaSedeEntity entity = zonaSedeJpaRepository.findById(zona.getId())
            .orElseThrow(() -> new IllegalArgumentException("ZonaSede not found"));
        PersistenceMapper.updateEntity(entity, zona);
        zonaSedeJpaRepository.save(entity);
    }

    @Override
    public void delete(ZonaSede zona) {
        zonaSedeJpaRepository.findById(zona.getId()).ifPresent(zonaSedeJpaRepository::delete);
    }
}
