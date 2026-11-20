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
  
}
  