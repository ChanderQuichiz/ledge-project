package com.ledgepay.catalogo.application.command;

public record RegistrarZonaCommand(
    String sedeId,
        String nombre,
    String tipo,
    Integer capacidad
) {

}

