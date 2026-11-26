package com.ledgepay.catalogo.domain;

import java.util.UUID;

public class PuntoAccesoSede {
    private UUID id;
    private UUID sedeId;
    private String nombre;
    public UUID getId() {
        return id;
    }
    public UUID getSedeId() {
        return sedeId;
    }
    public String getNombre() {
        return nombre;
    }

    public static PuntoAccesoSede reconstruir(UUID id, UUID sedeId, String nombre) {
        PuntoAccesoSede punto = new PuntoAccesoSede();
        punto.id = id;
        punto.sedeId = sedeId;
        punto.nombre = nombre;
        return punto;
    }
}
  