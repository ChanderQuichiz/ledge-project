package com.ledgepay.catalogo.application.usecases;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.ledgepay.catalogo.application.command.FechaEventoSuperpuestaException;
import com.ledgepay.catalogo.application.command.RegisterEventoCommand;
import com.ledgepay.catalogo.application.exceptions.DeudaVencidaException;
import com.ledgepay.catalogo.application.exceptions.OrganizadorNoVerificadoException;
import com.ledgepay.catalogo.application.exceptions.OrganizadorNotFoundException;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.application.port.out.PagosPort;
import com.ledgepay.catalogo.application.result.RegisterEventoResult;
import com.ledgepay.catalogo.domain.Evento;
import com.ledgepay.catalogo.domain.Organizador;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.ZonaSede;
import com.ledgepay.catalogo.domain.repositories.EventoRepository;
import com.ledgepay.catalogo.domain.repositories.OrganizadorRepository;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;
import com.ledgepay.catalogo.domain.value_objects.EstadoEvento;
import com.ledgepay.catalogo.domain.value_objects.EstadoOrganizador;
import com.ledgepay.catalogo.domain.value_objects.PoliticaCancelacion;
import com.ledgepay.catalogo.domain.value_objects.RestriccionEdad;

public class RegisterEventoUseCase {

    private final EventoRepository eventoRepository;
    private final SedeRepository sedeRepository;
    private final OrganizadorRepository organizadorRepository;
    private final PagosPort pagosPort;

    public RegisterEventoUseCase(EventoRepository eventoRepository, SedeRepository sedeRepository,
                                 OrganizadorRepository organizadorRepository, PagosPort pagosPort) {
        this.eventoRepository = eventoRepository;
        this.sedeRepository = sedeRepository;
        this.organizadorRepository = organizadorRepository;
        this.pagosPort = pagosPort;
    }

    @Transactional
    public RegisterEventoResult execute(RegisterEventoCommand command) {
        UUID sedeId = UUID.fromString(command.sedeId());
        UUID organizadorId = UUID.fromString(command.organizadorId());
        UUID creadoPor = UUID.fromString(command.creadoPor());

        RestriccionEdad restriccionEdad = RestriccionEdad.valueOf(command.restriccionEdad());
        PoliticaCancelacion politicaCancelacion = PoliticaCancelacion.valueOf(command.politicaCancelacion());

        LocalDateTime fechaInicio = parseFecha(command.fechaInicio());
        LocalDateTime fechaFin = parseFecha(command.fechaFin());
        LocalDateTime aperturaPuertas = parseFecha(command.aperturaPuertas());
        LocalDateTime fechaInicioVenta = parseFecha(command.fechaInicioVenta());
        LocalDateTime fechaFinVenta = parseFecha(command.fechaFinVenta());

        Sede sede = sedeRepository.findById(sedeId)
            .orElseThrow(() -> new SedeNotFoundException("No se encontró la sede con ID: " + sedeId));
        sede.zonasMinimasParaEvento();

        Organizador organizador = organizadorRepository.findById(organizadorId)
            .orElseThrow(() -> new OrganizadorNotFoundException("No se encontró el organizador con ID: " + organizadorId));
        if (organizador.getEstado() != EstadoOrganizador.VERIFICADO) {
            throw new OrganizadorNoVerificadoException("El organizador no está verificado y no puede registrar eventos");
        }
        if (Boolean.TRUE.equals(pagosPort.deudasVencidasPorOrganizador(organizadorId))) {
            throw new DeudaVencidaException("El organizador tiene deuda vencida y no puede registrar un nuevo evento");
        }

        boolean fechaSeSuperpone = eventoRepository.findAllBySedeId(sedeId).stream()
            .filter(evento -> evento.getEstado() != EstadoEvento.CANCELADO
                && evento.getEstado() != EstadoEvento.FINALIZADO)
            .anyMatch(evento -> fechaInicio.minusHours(2).isBefore(evento.getFechaFin().plusHours(2))
                && fechaFin.plusHours(2).isAfter(evento.getFechaInicio().minusHours(2)));
        if (fechaSeSuperpone) {
            throw new FechaEventoSuperpuestaException("La fecha del evento se superpone con otro evento en la misma sede.");
        }

        Evento evento = Evento.registrar(
            sedeId,
            organizadorId,
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
            creadoPor
        );

        eventoRepository.save(evento);

        List<ZonaSede> zonas = sede.getZonas() != null ? sede.getZonas() : List.of();

        return new RegisterEventoResult(
            evento.getId().toString(),
            evento.getOrganizadorId().toString(),
            new RegisterEventoResult.Sede(sede.getId().toString(), sede.getNombre(), sede.getCiudad()),
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
            texto(evento.getAperturaPuertas()),
            evento.getRestriccionEdad().name(),
            evento.getPoliticaCancelacion().name(),
            evento.getPermiteReingreso(),
            evento.getMaxReingresos(),
            evento.getEsNominativo(),
            evento.getLimiteCompraPorCliente(),
            evento.getEsExonerado(),
            texto(evento.getInicioVenta()),
            texto(evento.getFinVenta())
        );
    }

    private LocalDateTime parseFecha(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(valor).atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
        } catch (DateTimeParseException ignorada) {
            return LocalDateTime.parse(valor);
        }
    }

    private String texto(LocalDateTime fecha) {
        return fecha != null ? fecha.toString() : null;
    }
}
