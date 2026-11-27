package com.ledgepay.catalogo.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ledgepay.catalogo.domain.Organizador;
import com.ledgepay.catalogo.domain.repositories.OrganizadorRepository;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.mapper.PersistenceMapper;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.repository.OrganizadorJpaRepository;

@Component
public class OrganizadorRepositoryAdapter implements OrganizadorRepository {

    private final OrganizadorJpaRepository organizadorJpaRepository;

    public OrganizadorRepositoryAdapter(OrganizadorJpaRepository organizadorJpaRepository) {
        this.organizadorJpaRepository = organizadorJpaRepository;
    }

    @Override
    public Optional<Organizador> findById(UUID id) {
        return organizadorJpaRepository.findById(id).map(PersistenceMapper::toDomain);
    }
}
