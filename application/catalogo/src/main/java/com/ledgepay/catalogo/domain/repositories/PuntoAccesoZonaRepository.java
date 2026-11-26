package com.ledgepay.catalogo.domain.repositories;

import java.util.List;
import java.util.UUID;

import com.ledgepay.catalogo.domain.PuntoAccesoZona;

public interface PuntoAccesoZonaRepository {
    public List<PuntoAccesoZona> findByPuntoAcessoSedeId(UUID puntoAccesoId);
}
