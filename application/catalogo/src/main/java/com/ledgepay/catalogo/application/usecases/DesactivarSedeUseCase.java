package com.ledgepay.catalogo.application.usecases;

import java.util.UUID;

import org.springframework.transaction.support.TransactionTemplate;

import com.ledgepay.catalogo.application.command.DesacticarSedeCommand;
import com.ledgepay.catalogo.application.exceptions.DesactivacionSedeInvalida;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.application.port.out.EntradasClient;
import com.ledgepay.catalogo.application.query.EntradasByEventoQuery;
import com.ledgepay.catalogo.application.result.DesactivarSedeResult;
import com.ledgepay.catalogo.application.result.EntradasByEventoResult;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.repositories.PuntoAccesoZonaRepository;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;

public class DesactivarSedeUseCase {

    private final EntradasClient entradasClient;
    private final TransactionTemplate transactionTemplate;
    private final SedeRepository sedeRepository;
    private final PuntoAccesoZonaRepository puntoAccesoZonaRepository;

    public DesactivarSedeUseCase(EntradasClient entradasClient, TransactionTemplate transactionTemplate,
            SedeRepository sedeRepository, PuntoAccesoZonaRepository puntoAccesoZonaRepository) {
        this.entradasClient = entradasClient;
        this.transactionTemplate = transactionTemplate;
        this.sedeRepository = sedeRepository;
        this.puntoAccesoZonaRepository = puntoAccesoZonaRepository;
    }

    public DesactivarSedeResult execute(DesacticarSedeCommand command) {
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

    private DesactivarSedeResult desactivar(DesacticarSedeCommand command) {
        Sede sede = sedeRepository.findById(UUID.fromString(command.sedeId()))
            .orElseThrow(() -> new SedeNotFoundException("No se encontró la sede con ID: " + command.sedeId()));

        sede.desactivar();
        sedeRepository.save(sede);

        return new DesactivarSedeResult(
            sede.getId().toString(),
            sede.getNombre(),
            sede.getDireccion(),
            sede.getCiudad(),
            sede.getAforoMaximoLegal(),
            sede.getActiva() ? "Activa" : "Inactiva",
            sede.getZonas().stream()
                .map(zona -> new DesactivarSedeResult.ZonaResult(
                    zona.getId().toString(),
                    zona.getNombre(),
                    zona.getTipo().toString(),
                    zona.getCapacidad(),
                    zona.getActiva() ? "Activa" : "Inactiva"
                ))
                .toArray(DesactivarSedeResult.ZonaResult[]::new),
            sede.getPuntosAcceso().stream()
                .map(puntoAcceso -> new DesactivarSedeResult.PuntoAccesoResult(
                    puntoAcceso.getId().toString(),
                    puntoAcceso.getNombre(),
                    puntoAccesoZonaRepository.findByPuntoAcessoSedeId(puntoAcceso.getId()).stream()
                        .map(puntoAccesoZona -> puntoAccesoZona.getZonaId().toString())
                        .toArray(String[]::new)
                ))
                .toArray(DesactivarSedeResult.PuntoAccesoResult[]::new)
        );
    }
}
