package com.ledgepay.catalogo.application.usecases;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ledgepay.catalogo.application.command.RegisterEventoCommand;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.application.result.RegisterEventoResult;
import com.ledgepay.catalogo.domain.Evento;
import com.ledgepay.catalogo.domain.Sede;
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

        return new RegisterEventoResult();
    }
}