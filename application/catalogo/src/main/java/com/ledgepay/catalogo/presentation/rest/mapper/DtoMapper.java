package com.ledgepay.catalogo.presentation.rest.mapper;

import java.util.List;

import com.ledgepay.catalogo.application.command.DefinirAsientoCommand;
import com.ledgepay.catalogo.application.command.RegisterEventoCommand;
import com.ledgepay.catalogo.application.command.RegistrarSedeCommand;
import com.ledgepay.catalogo.application.command.RegistrarZonaCommand;
import com.ledgepay.catalogo.application.result.DefinirAsientoResult;
import com.ledgepay.catalogo.application.result.DesactivarSedeResult;
import com.ledgepay.catalogo.application.result.DesactivarZonaResult;
import com.ledgepay.catalogo.application.result.RegisterEventoResult;
import com.ledgepay.catalogo.application.result.RegistrarSedeResult;
import com.ledgepay.catalogo.application.result.RegistrarZonaResult;
import com.ledgepay.catalogo.domain.value_objects.RestriccionEdad;
import com.ledgepay.catalogo.presentation.rest.dto.AsientosDefinirRequest;
import com.ledgepay.catalogo.presentation.rest.dto.AsientosDefinidosResponse;
import com.ledgepay.catalogo.presentation.rest.dto.EventoCrearRequest;
import com.ledgepay.catalogo.presentation.rest.dto.EventoResponse;
import com.ledgepay.catalogo.presentation.rest.dto.PuntoAccesoResponse;
import com.ledgepay.catalogo.presentation.rest.dto.SedeCrearRequest;
import com.ledgepay.catalogo.presentation.rest.dto.SedeResponse;
import com.ledgepay.catalogo.presentation.rest.dto.ZonaSedeCrearRequest;
import com.ledgepay.catalogo.presentation.rest.dto.ZonaSedeResponse;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static RegistrarSedeCommand toCommand(SedeCrearRequest request) {
        return new RegistrarSedeCommand(request.nombre(), request.direccion(), request.ciudad(), request.aforoMaximo());
    }

    public static RegistrarZonaCommand toCommand(String sedeId, ZonaSedeCrearRequest request) {
        return new RegistrarZonaCommand(sedeId, request.nombre(), request.tipo(), request.capacidad());
    }

    public static DefinirAsientoCommand toCommand(String sedeId, String zonaId, AsientosDefinirRequest request) {
        return new DefinirAsientoCommand(sedeId, zonaId, request.asientos().stream()
            .map(asiento -> new DefinirAsientoCommand.Asiento(
                asiento.fila(),
                asiento.numero(),
                !Boolean.TRUE.equals(asiento.inhabilitado()),
                Boolean.TRUE.equals(asiento.accesible())))
            .toList());
    }

    public static RegisterEventoCommand toCommand(EventoCrearRequest request) {
        return new RegisterEventoCommand(
            request.sedeId(),
            request.organizadorId(),
            request.nombre(),
            request.descripcion(),
            request.categoria(),
            request.fechaInicio(),
            request.fechaFin(),
            request.aperturaPuertas(),
            restriccionEdad(request.restriccionEdad()),
            request.politicaCancelacion(),
            request.permiteReingreso(),
            request.maxReingresos(),
            request.esNominativo(),
            request.limiteCompraPorCliente(),
            request.eventoExonerado(),
            request.fechaInicioVenta(),
            request.fechaFinVenta(),
            request.creadoPor());
    }

    public static SedeResponse toResponse(RegistrarSedeResult result) {
        return new SedeResponse(result.sedeId(), result.nombre(), result.direccion(), result.ciudad(),
            result.aforoMaximo(), result.estado(), List.of(), List.of());
    }

    public static SedeResponse toResponse(DesactivarSedeResult result) {
        List<ZonaSedeResponse> zonas = java.util.Arrays.stream(result.zonas())
            .map(zona -> new ZonaSedeResponse(zona.zonaId(), zona.nombre(), zona.tipo(), zona.capacidad(), zona.estado()))
            .toList();
        List<PuntoAccesoResponse> puntosAcceso = java.util.Arrays.stream(result.puntosAcceso())
            .map(punto -> new PuntoAccesoResponse(punto.puntoAccesoId(), punto.nombre(), List.of(punto.zonaIds())))
            .toList();
        return new SedeResponse(result.sedeId(), result.nombre(), result.direccion(), result.ciudad(),
            result.aforoMaximo(), result.estado(), zonas, puntosAcceso);
    }

    public static ZonaSedeResponse toResponse(RegistrarZonaResult result) {
        return new ZonaSedeResponse(result.zonaId(), result.nombre(), result.tipo(), result.capacidad(), result.estado());
    }

    public static ZonaSedeResponse toResponse(DesactivarZonaResult result) {
        return new ZonaSedeResponse(result.zonaId(), result.nombre(), result.tipo(), result.capacidad(), result.estado());
    }

    public static AsientosDefinidosResponse toResponse(DefinirAsientoResult result) {
        return new AsientosDefinidosResponse(result.zonaId(), result.total());
    }

    public static EventoResponse toResponse(RegisterEventoResult result) {
        return new EventoResponse(
            result.eventoId(),
            result.organizadorId(),
            new EventoResponse.SedeResponseMinima(result.sede().sedeId(), result.sede().nombre(), result.sede().ciudad()),
            result.zonas().stream()
                .map(zona -> new EventoResponse.ZonaEventoResponse(
                    zona.zonaId(), zona.nombre(), zona.tipo(), zona.capacidadOfrecida(),
                    zona.precioVigente(), zona.faseVigente(), zona.tarifaDinamica()))
                .toList(),
            result.reprogramaciones(),
            result.estado(),
            result.nombre(),
            result.descripcion(),
            result.categoria(),
            result.fechaInicio(),
            result.fechaFin(),
            result.aperturaPuertas(),
            result.restriccionEdad(),
            result.politicaCancelacion(),
            result.permiteReingreso(),
            result.maxReingresos(),
            result.esNominativo(),
            result.limiteCompraPorCliente(),
            result.eventoExonerado(),
            result.fechaInicioVenta(),
            result.fechaFinVenta());
    }

    private static String restriccionEdad(String value) {
        return switch (value) {
            case "+14" -> RestriccionEdad.MAYOR_DE_14.name();
            case "+18" -> RestriccionEdad.MAYOR_DE_18.name();
            default -> value;
        };
    }
}
