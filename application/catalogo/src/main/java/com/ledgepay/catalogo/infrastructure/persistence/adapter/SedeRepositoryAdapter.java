package com.ledgepay.catalogo.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.entity.SedeEntity;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.mapper.PersistenceMapper;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.repository.SedeJpaRepository;

@Component
public class SedeRepositoryAdapter implements SedeRepository {

    private final SedeJpaRepository sedeJpaRepository;

    public SedeRepositoryAdapter(SedeJpaRepository sedeJpaRepository) {
        this.sedeJpaRepository = sedeJpaRepository;
    }

    @Override
    public Optional<Sede> findById(UUID id) {
        return sedeJpaRepository.findById(id).map(PersistenceMapper::toDomain);
    }

    @Override
    public Sede save(Sede sede) {
        SedeEntity entity = sedeJpaRepository.findById(sede.getId()).orElse(null);
        if (entity == null) {
            entity = PersistenceMapper.toEntity(sede);
        } else {
            PersistenceMapper.updateEntity(entity, sede);
        }
        return PersistenceMapper.toDomain(sedeJpaRepository.save(entity));
    }

    @Override
    public void delete(Sede sede) {
        sedeJpaRepository.findById(sede.getId()).ifPresent(sedeJpaRepository::delete);
    }
}
