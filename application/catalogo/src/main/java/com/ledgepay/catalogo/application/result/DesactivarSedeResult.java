package com.ledgepay.catalogo.application.result;

public record DesactivarSedeResult(
    String sedeId,
    String nombre,
    String direccion,
    String ciudad,
    Integer aforoMaximo,
    String estado,
    ZonaResult[] zonas,
    PuntoAccesoResult[] puntosAcceso
) {
    public record ZonaResult(
        String zonaId,
        String nombre,
        String tipo,
        Integer capacidad,
        String estado
    ) {
    }

    public record PuntoAccesoResult(
        String puntoAccesoId,
        String nombre,
        String[] zonaIds
    ) {
    }
}
