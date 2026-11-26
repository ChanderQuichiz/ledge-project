package com.ledgepay.catalogo.domain.repositories;

import java.util.Optional;
import java.util.UUID;

import com.ledgepay.catalogo.domain.Evento;

public interface EventoRepository {
    Evento save(Evento evento);
    Optional<Evento> findById(UUID id);
    Optional<Evento> findBySedeId(UUID fromString);
}
