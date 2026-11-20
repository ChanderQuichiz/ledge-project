package com.ledgepay.catalogo.application.command;

import java.util.List;

public record DefinirAsientoCommand(
    String sedeId,
    String zonaId,
    List<Asiento> asientos
) {
    public record Asiento(
        String fila,
        Integer numero,
        Boolean habilitado,
        Boolean accesible
    ) {
    }
}



