package com.ledgepay.catalogo.domain.repositories;

import java.util.Optional;
import java.util.UUID;

import com.ledgepay.catalogo.domain.Sede;


public interface SedeRepository {
    public Optional<Sede> findById(UUID id);
    public Sede save(Sede sede);
    public void delete(Sede sede);
}
