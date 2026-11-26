package com.ledgepay.catalogo.presentation.rest.dto;

import java.util.List;

public record EventoResponse(
    String eventoId,
    String organizadorId,
    SedeResponseMinima sede,
    List<ZonaEventoResponse> zonas,
    Integer reprogramaciones,
    String estado,
    String nombre,
    String descripcion,
    String categoria,
    String fechaInicio,
    String fechaFin,
    String aperturaPuertas,
    String restriccionEdad,
    String politicaCancelacion,
    Boolean permiteReingreso,
    Integer maxReingresos,
    Boolean esNominativo,
    Integer limiteCompraPorCliente,
    Boolean eventoExonerado,
    String fechaInicioVenta,
    String fechaFinVenta
) {
    public record SedeResponseMinima(
        String sedeId,
        String nombre,
        String ciudad
    ) {
    }

    public record ZonaEventoResponse(
        String zonaId,
        String nombre,
        String tipo,
        Integer capacidadOfrecida,
        Double precioVigente,
        String faseVigente,
        Boolean tarifaDinamica
    ) {
    }
}
