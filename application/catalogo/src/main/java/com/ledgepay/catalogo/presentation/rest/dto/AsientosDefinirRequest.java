package com.ledgepay.catalogo.presentation.rest.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AsientosDefinirRequest(
    @NotEmpty List<@Valid Asiento> asientos
) {
    public record Asiento(
        @NotBlank String fila,
        @NotNull @Positive Integer numero,
        Boolean inhabilitado,
        Boolean accesible
    ) {
    }
}
