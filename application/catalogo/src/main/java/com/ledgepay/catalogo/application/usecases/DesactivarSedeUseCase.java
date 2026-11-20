package com.ledgepay.catalogo.application.usecases;

import java.util.UUID;

import com.ledgepay.catalogo.application.command.DesacticarSedeCommand;
import com.ledgepay.catalogo.application.exceptions.DesactivacionSedeInvalida;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.application.feign.EntradasFeign;
import com.ledgepay.catalogo.application.query.EntradasByEventoQuery;
import com.ledgepay.catalogo.application.result.DesactivarSedeResult;
import com.ledgepay.catalogo.application.result.EntradasByEventoResult;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.repositories.PuntoAccesoZonaRepository;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;

public class DesactivarSedeUseCase {

private final EntradasFeign entradasFeign;
private final SedeRepository sedeRepository;
private final PuntoAccesoZonaRepository puntoAccesoZonaRepository;
    public DesactivarSedeUseCase(EntradasFeign entradasFeign, SedeRepository sedeRepository, PuntoAccesoZonaRepository puntoAccesoZonaRepository) {
        this.entradasFeign = entradasFeign;
        this.sedeRepository = sedeRepository;
        this.puntoAccesoZonaRepository = puntoAccesoZonaRepository;
    }

 public DesactivarSedeResult execute(DesacticarSedeCommand command) {
    EntradasByEventoResult result = this.entradasFeign.getEntradasByEvento(
        new EntradasByEventoQuery(command.sedeId())
    );

    // 1. Negar la condición: lanzar excepción si NO está vacía
    if (!result.entradas().isEmpty()) {
        throw new DesactivacionSedeInvalida(
            "No se puede desactivar la sede, ya que existen entradas asociadas a ella."
        );
    }

    Sede sede = this.sedeRepository.findById(UUID.fromString(command.sedeId())).orElseThrow(()-> new SedeNotFoundException("No se encontró la sede con ID: " + command.sedeId()) );
    sede.desactivar();

    // 2. Persistir el cambio en la base de datos
    this.sedeRepository.save(sede);

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
                zona.getTipo().toString(), // Ajusta si ya es String
                zona.getCapacidad(),
                zona.getActiva() ? "Activa" : "Inactiva" // 3. Faltaba este 5to parámetro (estado)
            ))
            .toArray(DesactivarSedeResult.ZonaResult[]::new),
    sede.getPuntosAcceso().stream()
    .map(puntoAcceso -> new DesactivarSedeResult.PuntoAccesoResult(
        puntoAcceso.getId().toString(),
        puntoAcceso.getNombre(),
        this.puntoAccesoZonaRepository.findByPuntoAcessoSedeId(puntoAcceso.getId()).stream().map(paz -> paz.getZonaId()).toArray(String[]::new)
    ))  
    .toArray(DesactivarSedeResult.PuntoAccesoResult[]::new));
}
}
