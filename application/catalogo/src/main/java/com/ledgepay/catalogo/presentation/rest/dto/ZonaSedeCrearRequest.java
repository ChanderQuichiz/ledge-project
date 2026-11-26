package com.ledgepay.catalogo.presentation.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ZonaSedeCrearRequest(
    @NotBlank String nombre,
    @NotBlank String tipo,
    @NotNull @Positive Integer capacidad
) {
}
