package com.ledgepay.catalogo.application.usecases;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.ledgepay.catalogo.application.command.RegisterEventoCommand;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.application.result.RegisterEventoResult;
import com.ledgepay.catalogo.domain.Evento;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.ZonaSede;
import com.ledgepay.catalogo.domain.repositories.EventoRepository;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;
import com.ledgepay.catalogo.domain.value_objects.PoliticaCancelacion;
import com.ledgepay.catalogo.domain.value_objects.RestriccionEdad;

public class RegisterEventoUseCase {
    
    private final EventoRepository eventoRepository;
    private final SedeRepository sedeRepository;

    public RegisterEventoUseCase(EventoRepository eventoRepository, SedeRepository sedeRepository) {
        this.eventoRepository = eventoRepository;
        this.sedeRepository = sedeRepository;
    }

    @Transactional
    public RegisterEventoResult execute(RegisterEventoCommand command) {
        Sede sede = this.sedeRepository.findById(UUID.fromString(command.sedeId())).orElseThrow(() -> new SedeNotFoundException("No se encontró la sede con ID: " + command.sedeId()));
        sede.zonasMinimasParaEvento();

        RestriccionEdad restriccionEdad = RestriccionEdad.valueOf(command.restriccionEdad());
        PoliticaCancelacion politicaCancelacion = PoliticaCancelacion.valueOf(command.politicaCancelacion());
        
        LocalDateTime fechaInicio = LocalDateTime.parse(command.fechaInicio());
        LocalDateTime fechaFin = LocalDateTime.parse(command.fechaFin());
        LocalDateTime aperturaPuertas = command.aperturaPuertas() != null 
                ? LocalDateTime.parse(command.aperturaPuertas()) 
                : null;
        LocalDateTime fechaInicioVenta = LocalDateTime.parse(command.fechaInicioVenta());
        LocalDateTime fechaFinVenta = LocalDateTime.parse(command.fechaFinVenta());

        Evento evento = Evento.registrar(
            UUID.fromString(command.sedeId()),
            UUID.fromString(command.organizadorId()),
            command.nombre(),
            command.descripcion(),
            command.categoria(),
            restriccionEdad,
            politicaCancelacion,
            command.permiteReingreso(),
            command.maxReingresos(),
            command.esNominativo(),
            command.eventoExonerado(),
            command.limiteCompraPorCliente(),
            fechaInicio,
            fechaFin,
            aperturaPuertas,
            fechaInicioVenta,
            fechaFinVenta,
            UUID.fromString(command.creadoPor()) // Asegúrate de que este campo exista en RegisterEventoCommand
        );

        this.eventoRepository.save(evento);

        List<ZonaSede> zonas = sede.getZonas() != null ? sede.getZonas() : List.of();

        return new RegisterEventoResult(
            evento.getId().toString(),
            evento.getOrganizadorId().toString(),
            new RegisterEventoResult.Sede(
                sede.getId().toString(),
                sede.getNombre(),
                sede.getCiudad()
            ),
            zonas.stream()
                .map(zona -> new RegisterEventoResult.Zona(
                    zona.getId().toString(),
                    zona.getNombre(),
                    zona.getTipo().name(),
                    zona.getCapacidad(),
                    null,
                    null,
                    false
                ))
                .toList(),
            evento.getNumeroReprogramaciones(),
            evento.getEstado().name(),
            evento.getNombre(),
            evento.getDescripcion(),
            evento.getCategoria(),
            evento.getFechaInicio().toString(),
            evento.getFechaFin().toString(),
            evento.getAperturaPuertas() != null ? evento.getAperturaPuertas().toString() : null,
            evento.getRestriccionEdad().name(),
            evento.getPoliticaCancelacion().name(),
            evento.getPermiteReingreso(),
            evento.getMaxReingresos(),
            evento.getEsNominativo(),
            evento.getLimiteCompraPorCliente(),
            evento.getEsExonerado(),
            evento.getInicioVenta().toString(),
            evento.getFinVenta().toString()
        );
    }
}