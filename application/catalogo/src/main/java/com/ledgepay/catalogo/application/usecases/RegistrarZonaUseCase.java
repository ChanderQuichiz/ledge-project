package com.ledgepay.catalogo.application.usecases;

import java.util.UUID;

import com.ledgepay.catalogo.application.command.RegistrarZonaCommand;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.application.result.RegistrarZonaResult;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.ZonaSede;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;
import com.ledgepay.catalogo.domain.value_objects.TipoZona;

public class RegistrarZonaUseCase {
    private final SedeRepository sedeRepository;
    public RegistrarZonaUseCase(SedeRepository sedeRepository) {
        this.sedeRepository = sedeRepository;
    }
    public RegistrarZonaResult execute(RegistrarZonaCommand command) {
        // Implement the logic to register a zone here

        Sede sede = sedeRepository.findById(UUID.fromString(command.sedeId()))
                .orElseThrow(() -> new SedeNotFoundException("Sede not found with id: " + command.sedeId()));

                ZonaSede zona = ZonaSede.crear(UUID.fromString(command.sedeId()), command.nombre(), TipoZona.valueOf(command.tipo()), command.capacidad());

        sede.RegistrarZona(zona);

   return new RegistrarZonaResult(
            zona.getId().toString(),
            zona.getNombre(),
            zona.getTipo().name(),
            zona.getCapacidad(),
            zona.getActiva() ? "ACTIVA" : "INACTIVA"
        );

        // For now, return a dummy result
    }
}
