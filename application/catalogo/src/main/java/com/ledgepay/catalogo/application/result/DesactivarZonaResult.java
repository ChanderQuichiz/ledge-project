package com.ledgepay.catalogo.application.result;

public record DesactivarZonaResult(
    String zonaId,
    String nombre,
    String tipo,
    int capacidad,
    String estado
) {
    
}

