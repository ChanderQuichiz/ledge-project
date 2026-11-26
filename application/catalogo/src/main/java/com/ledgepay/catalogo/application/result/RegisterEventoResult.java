package com.ledgepay.catalogo.application.result;

import java.util.List;

public record RegisterEventoResult(
    String eventoId,
    String organizadorId,
    Sede sede,
    List<Zona> zonas,
    int reprogramaciones,
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
    public record Sede(
        String sedeId,
        String nombre,
        String ciudad
    ) {}

    public record Zona(
        String zonaId,
        String nombre,
        String tipo,
        Integer capacidadOfrecida,
        Double precioVigente,
        String faseVigente,
        Boolean tarifaDinamica
    ) {}
}
