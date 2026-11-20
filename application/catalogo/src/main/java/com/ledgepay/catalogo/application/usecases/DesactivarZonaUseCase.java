package com.ledgepay.catalogo.application.usecases;

import java.util.UUID;

import com.ledgepay.catalogo.application.command.DesactivarZonaCommand;
import com.ledgepay.catalogo.application.exceptions.DesactivacionSedeInvalida;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.application.feign.EntradasFeign;
import com.ledgepay.catalogo.application.query.EntradasByEventoQuery;
import com.ledgepay.catalogo.application.result.DesactivarZonaResult;
import com.ledgepay.catalogo.application.result.EntradasByEventoResult;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.ZonaSede;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;

public class DesactivarZonaUseCase {
    private final SedeRepository sedeRepository;
    private final EntradasFeign entradasFeign;
    public DesactivarZonaUseCase(SedeRepository sedeRepository, EntradasFeign entradasFeign) {
        this.sedeRepository = sedeRepository;
        this.entradasFeign = entradasFeign;
    }
    public DesactivarZonaResult execute(DesactivarZonaCommand command) {
        // Lógica para desactivar la zona
        // Aquí puedes implementar la lógica necesaria para desactivar la zona en tu sistema.
        // Por ejemplo, podrías buscar la zona por su ID y cambiar su estado a "desactivada".
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
   
    ZonaSede zona = sede.getZonas().stream()
        .filter(z -> z.getId().toString().equals(command.zonaId()))
        .findFirst()
        .orElseThrow(() -> new SedeNotFoundException("No se encontró la zona con ID: " + command.zonaId()));

zona.desactivar();

    this.sedeRepository.save(sede);

    
        // Retornar un resultado indicando que la zona ha sido desactivada.
        return new DesactivarZonaResult(
            zona.getId().toString(),
            zona.getNombre(), // Aquí deberías obtener el nombre real de la zona
            zona.getTipo().toString(),      // Aquí deberías obtener el tipo real de la zona
            zona.getCapacidad(),                 // Aquí deberías obtener la capacidad real de la zona
            zona.getActiva()? "ACTIVA" : "DESACTIVADA"       // Estado actualizado de la zona
        );
    }
}
