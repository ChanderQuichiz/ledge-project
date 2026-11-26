package com.ledgepay.catalogo.application.usecases;

import java.util.UUID;

import org.springframework.transaction.support.TransactionTemplate;

import com.ledgepay.catalogo.application.command.DesactivarZonaCommand;
import com.ledgepay.catalogo.application.exceptions.DesactivacionSedeInvalida;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.application.port.out.EntradasClient;
import com.ledgepay.catalogo.application.query.EntradasByEventoQuery;
import com.ledgepay.catalogo.application.result.DesactivarZonaResult;
import com.ledgepay.catalogo.application.result.EntradasByEventoResult;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.ZonaSede;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;

public class DesactivarZonaUseCase {

    private final SedeRepository sedeRepository;
    private final EntradasClient entradasClient;
    private final TransactionTemplate transactionTemplate;

    public DesactivarZonaUseCase(SedeRepository sedeRepository, EntradasClient entradasClient,
            TransactionTemplate transactionTemplate) {
        this.sedeRepository = sedeRepository;
        this.entradasClient = entradasClient;
        this.transactionTemplate = transactionTemplate;
    }

    public DesactivarZonaResult execute(DesactivarZonaCommand command) {
        EntradasByEventoResult resultado = entradasClient.getEntradasByEvento(
            new EntradasByEventoQuery(command.sedeId())
        );

        if (!resultado.entradas().isEmpty()) {
            throw new DesactivacionSedeInvalida(
                "No se puede desactivar la sede, ya que existen entradas asociadas a ella."
            );
        }

        return transactionTemplate.execute(status -> desactivar(command));
    }

    private DesactivarZonaResult desactivar(DesactivarZonaCommand command) {
        Sede sede = sedeRepository.findById(UUID.fromString(command.sedeId()))
            .orElseThrow(() -> new SedeNotFoundException("No se encontró la sede con ID: " + command.sedeId()));

        ZonaSede zona = sede.getZonas().stream()
            .filter(unaZona -> unaZona.getId().toString().equals(command.zonaId()))
            .findFirst()
            .orElseThrow(() -> new SedeNotFoundException("No se encontró la zona con ID: " + command.zonaId()));

        zona.desactivar();
        sedeRepository.save(sede);

        return new DesactivarZonaResult(
            zona.getId().toString(),
            zona.getNombre(),
            zona.getTipo().toString(),
            zona.getCapacidad(),
            zona.getActiva() ? "ACTIVA" : "DESACTIVADA"
        );
    }
}
