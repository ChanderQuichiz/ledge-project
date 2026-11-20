package com.ledgepay.catalogo.application.command;

public record RegisterEventoCommand(
    String sedeId,
    String organizadorId,
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
    String fechaFinVenta,
    String creadoPor
) {
    
}
