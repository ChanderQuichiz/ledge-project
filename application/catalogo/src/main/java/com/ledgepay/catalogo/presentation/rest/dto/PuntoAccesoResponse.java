package com.ledgepay.catalogo.presentation.rest.dto;

import java.util.List;

public record PuntoAccesoResponse(
    String puntoAccesoId,
    String nombre,
    List<String> zonaIds
) {
}
