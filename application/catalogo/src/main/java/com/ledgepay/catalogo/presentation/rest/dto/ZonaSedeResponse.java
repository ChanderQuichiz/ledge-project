package com.ledgepay.catalogo.presentation.rest.dto;

public record ZonaSedeResponse(
    String zonaId,
    String nombre,
    String tipo,
    Integer capacidad,
    String estado
) {
}
