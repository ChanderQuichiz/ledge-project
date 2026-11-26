package com.ledgepay.catalogo.domain.repositories;

import java.util.UUID;

import com.ledgepay.catalogo.domain.FaseVenta;

public interface FaseVentaRepository {
    public FaseVenta findByEventoId(UUID eventoId);

    void save(FaseVenta faseVenta);
}
