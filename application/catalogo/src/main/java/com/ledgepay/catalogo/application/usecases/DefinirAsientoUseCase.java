package com.ledgepay.catalogo.application.usecases;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.ledgepay.catalogo.application.command.DefinirAsientoCommand;
import com.ledgepay.catalogo.application.result.DefinirAsientoResult;
import com.ledgepay.catalogo.domain.AsientoZona;
import com.ledgepay.catalogo.domain.ZonaSede;
import com.ledgepay.catalogo.domain.exceptions.ZonaNotFoundException;
import com.ledgepay.catalogo.domain.repositories.ZonaSedeRepository;

public class DefinirAsientoUseCase {

private final ZonaSedeRepository zonaSedeRepository;
public DefinirAsientoUseCase(ZonaSedeRepository zonaSedeRepository) {
        this.zonaSedeRepository = zonaSedeRepository;
    }

    @Transactional
    public DefinirAsientoResult execute(DefinirAsientoCommand command) {
        // Implement the logic to define seats based on the command
        
        ZonaSede zonaSede = this.zonaSedeRepository.findById(UUID.fromString(command.zonaId())).orElseThrow(() -> new ZonaNotFoundException("No se encontró la zona con ID: " + command.zonaId()));
        

        List<AsientoZona> asientos = command.asientos().stream()
                .map(a -> AsientoZona.crear(UUID.fromString(command.zonaId()), a.fila(), a.numero(), a.accesible(), a.habilitado()))
                .collect(Collectors.toList());
        zonaSede.definirAsientos(asientos);
        zonaSedeRepository.save(zonaSede);


        // For now, return a dummy result
        return new DefinirAsientoResult(command.zonaId(), command.asientos().size());
    }
}
