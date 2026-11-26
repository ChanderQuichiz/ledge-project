package com.ledgepay.catalogo.presentation.rest.dto;

import java.util.List;

public record SedeResponse(
    String sedeId,
    String nombre,
    String direccion,
    String ciudad,
    Integer aforoMaximo,
    String estado,
    List<ZonaSedeResponse> zonas,
    List<PuntoAccesoResponse> puntosAcceso
) {
}
