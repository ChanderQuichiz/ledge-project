package com.ledgepay.catalogo.presentation.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventoCrearRequest(
    @NotBlank String sedeId,
    @NotBlank String organizadorId,
    @NotBlank String creadoPor,
    @NotBlank String nombre,
    String descripcion,
    @NotBlank String categoria,
    @NotBlank String fechaInicio,
    @NotBlank String fechaFin,
    @NotBlank String aperturaPuertas,
    @NotBlank String restriccionEdad,
    @NotBlank String politicaCancelacion,
    @NotNull Boolean permiteReingreso,
    Integer maxReingresos,
    @NotNull Boolean esNominativo,
    Integer limiteCompraPorCliente,
    Boolean eventoExonerado,
    String fechaInicioVenta,
    String fechaFinVenta
) {
}
