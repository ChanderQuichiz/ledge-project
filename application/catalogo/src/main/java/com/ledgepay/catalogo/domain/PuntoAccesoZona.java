package com.ledgepay.catalogo.domain;

import java.util.UUID;

public class PuntoAccesoZona {
    private UUID puntoAccesoId;
    private UUID zonaId;

private PuntoAccesoSede puntoAcceso;
private ZonaSede zona;
    public ZonaSede getZona() {
    return zona;
}

    public PuntoAccesoSede getPuntoAcceso() {
        return puntoAcceso;
    }

public UUID getPuntoAccesoId() {
        return puntoAccesoId;
    }

   
    public UUID getZonaId() {
        return zonaId;
    }


}



/*

  punto_acceso_id uuid [not null, ref: > puntos_acceso_sede.id]
  zona_id uuid [not null, ref: > zonas_sede.id]

*/