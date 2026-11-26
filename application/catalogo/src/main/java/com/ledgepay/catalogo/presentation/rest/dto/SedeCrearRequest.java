package com.ledgepay.catalogo.presentation.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SedeCrearRequest(
    @NotBlank String nombre,
    @NotBlank String direccion,
    @NotBlank String ciudad,
    @NotNull @Positive Integer aforoMaximo
) {
}
