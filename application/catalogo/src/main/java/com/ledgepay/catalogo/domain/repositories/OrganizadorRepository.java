package com.ledgepay.catalogo.domain.repositories;

import java.util.Optional;
import java.util.UUID;

import com.ledgepay.catalogo.domain.Organizador;

public interface OrganizadorRepository {

    Optional<Organizador> findById(UUID fromString);

    
}
