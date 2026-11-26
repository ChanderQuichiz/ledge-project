package com.ledgepay.catalogo.application.result;

public record RegistrarZonaResult(
    String zonaId,
    String nombre,
    String tipo,
    Integer capacidad,
    String estado
) {
    
}

