package com.ledgepay.catalogo.domain.repositories;

import java.util.Optional;
import java.util.UUID;

import com.ledgepay.catalogo.domain.ZonaSede;

public interface ZonaSedeRepository {

    Optional<ZonaSede> findById(UUID fromString);

    void save(ZonaSede zona);

    void delete(ZonaSede zona);
    
}
