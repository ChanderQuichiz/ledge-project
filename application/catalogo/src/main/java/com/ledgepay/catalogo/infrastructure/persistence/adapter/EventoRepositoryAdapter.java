package com.ledgepay.catalogo.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ledgepay.catalogo.domain.Evento;
import com.ledgepay.catalogo.domain.repositories.EventoRepository;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.mapper.PersistenceMapper;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.repository.EventoJpaRepository;

@Component
public class EventoRepositoryAdapter implements EventoRepository {

    private final EventoJpaRepository eventoJpaRepository;

    public EventoRepositoryAdapter(EventoJpaRepository eventoJpaRepository) {
        this.eventoJpaRepository = eventoJpaRepository;
    }

    @Override
    public Evento save(Evento evento) {
        return PersistenceMapper.toDomain(eventoJpaRepository.save(PersistenceMapper.toEntity(evento)));
    }

    @Override
    public Optional<Evento> findById(UUID id) {
        return eventoJpaRepository.findById(id).map(PersistenceMapper::toDomain);
    }

    @Override
    public Optional<Evento> findBySedeId(UUID sedeId) {
        return eventoJpaRepository.findBySedeId(sedeId).map(PersistenceMapper::toDomain);
    }

    @Override
    public List<Evento> findAllBySedeId(UUID sedeId) {
        return eventoJpaRepository.findAllBySedeId(sedeId).stream().map(PersistenceMapper::toDomain).toList();
    }
}
