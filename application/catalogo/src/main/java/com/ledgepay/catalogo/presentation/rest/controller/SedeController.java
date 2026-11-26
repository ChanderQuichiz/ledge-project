package com.ledgepay.catalogo.presentation.rest.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ledgepay.catalogo.application.command.DefinirAsientoCommand;
import com.ledgepay.catalogo.application.command.DesacticarSedeCommand;
import com.ledgepay.catalogo.application.command.DesactivarZonaCommand;
import com.ledgepay.catalogo.application.command.EliminarSedeCommand;
import com.ledgepay.catalogo.application.command.EliminarZonaCommand;
import com.ledgepay.catalogo.application.command.RegistrarSedeCommand;
import com.ledgepay.catalogo.application.command.RegistrarZonaCommand;
import com.ledgepay.catalogo.application.result.DefinirAsientoResult;
import com.ledgepay.catalogo.application.result.DesactivarSedeResult;
import com.ledgepay.catalogo.application.result.DesactivarZonaResult;
import com.ledgepay.catalogo.application.result.RegistrarSedeResult;
import com.ledgepay.catalogo.application.result.RegistrarZonaResult;
import com.ledgepay.catalogo.application.usecases.DefinirAsientoUseCase;
import com.ledgepay.catalogo.application.usecases.DesactivarSedeUseCase;
import com.ledgepay.catalogo.application.usecases.DesactivarZonaUseCase;
import com.ledgepay.catalogo.application.usecases.EliminarSedeUseCase;
import com.ledgepay.catalogo.application.usecases.EliminarZonaUseCase;
import com.ledgepay.catalogo.application.usecases.RegistrarSedeUseCase;
import com.ledgepay.catalogo.application.usecases.RegistrarZonaUseCase;
import com.ledgepay.catalogo.presentation.rest.dto.AsientosDefinidosResponse;
import com.ledgepay.catalogo.presentation.rest.dto.AsientosDefinirRequest;
import com.ledgepay.catalogo.presentation.rest.dto.SedeCrearRequest;
import com.ledgepay.catalogo.presentation.rest.dto.SedeResponse;
import com.ledgepay.catalogo.presentation.rest.dto.ZonaSedeCrearRequest;
import com.ledgepay.catalogo.presentation.rest.dto.ZonaSedeResponse;
import com.ledgepay.catalogo.presentation.rest.mapper.DtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/sedes")
public class SedeController {

    private final RegistrarSedeUseCase registrarSedeUseCase;
    private final RegistrarZonaUseCase registrarZonaUseCase;
    private final DefinirAsientoUseCase definirAsientoUseCase;
    private final DesactivarSedeUseCase desactivarSedeUseCase;
    private final DesactivarZonaUseCase desactivarZonaUseCase;
    private final EliminarSedeUseCase eliminarSedeUseCase;
    private final EliminarZonaUseCase eliminarZonaUseCase;

    public SedeController(RegistrarSedeUseCase registrarSedeUseCase, RegistrarZonaUseCase registrarZonaUseCase,
            DefinirAsientoUseCase definirAsientoUseCase, DesactivarSedeUseCase desactivarSedeUseCase,
            DesactivarZonaUseCase desactivarZonaUseCase, EliminarSedeUseCase eliminarSedeUseCase,
            EliminarZonaUseCase eliminarZonaUseCase) {
        this.registrarSedeUseCase = registrarSedeUseCase;
        this.registrarZonaUseCase = registrarZonaUseCase;
        this.definirAsientoUseCase = definirAsientoUseCase;
        this.desactivarSedeUseCase = desactivarSedeUseCase;
        this.desactivarZonaUseCase = desactivarZonaUseCase;
        this.eliminarSedeUseCase = eliminarSedeUseCase;
        this.eliminarZonaUseCase = eliminarZonaUseCase;
    }

    @PostMapping
    public ResponseEntity<SedeResponse> crearSede(@Valid @RequestBody SedeCrearRequest request) {
        RegistrarSedeCommand command = DtoMapper.toCommand(request);
        RegistrarSedeResult result = registrarSedeUseCase.execute(command);
        SedeResponse response = DtoMapper.toResponse(result);
        return ResponseEntity.created(URI.create("/api/v1/sedes/" + response.sedeId())).body(response);
    }

    @PostMapping("/{sedeId}/zonas")
    public ResponseEntity<ZonaSedeResponse> crearZona(@PathVariable String sedeId,
            @Valid @RequestBody ZonaSedeCrearRequest request) {
        RegistrarZonaResult result = registrarZonaUseCase.execute(DtoMapper.toCommand(sedeId, request));
        return ResponseEntity.created(URI.create("/api/v1/sedes/" + sedeId + "/zonas/" + result.zonaId()))
            .body(DtoMapper.toResponse(result));
    }

    @PutMapping("/{sedeId}/zonas/{zonaId}/asientos")
    public ResponseEntity<AsientosDefinidosResponse> definirAsientos(@PathVariable String sedeId,
            @PathVariable String zonaId, @Valid @RequestBody AsientosDefinirRequest request) {
        DefinirAsientoCommand command = DtoMapper.toCommand(sedeId, zonaId, request);
        DefinirAsientoResult result = definirAsientoUseCase.execute(command);
        return ResponseEntity.ok(DtoMapper.toResponse(result));
    }

    @PostMapping("/{sedeId}/desactivacion")
    public ResponseEntity<SedeResponse> desactivarSede(@PathVariable String sedeId) {
        DesactivarSedeResult result = desactivarSedeUseCase.execute(new DesacticarSedeCommand(sedeId));
        return ResponseEntity.ok(DtoMapper.toResponse(result));
    }

    @PostMapping("/{sedeId}/zonas/{zonaId}/desactivacion")
    public ResponseEntity<ZonaSedeResponse> desactivarZona(@PathVariable String sedeId,
            @PathVariable String zonaId) {
        DesactivarZonaResult result = desactivarZonaUseCase.execute(new DesactivarZonaCommand(sedeId, zonaId));
        return ResponseEntity.ok(DtoMapper.toResponse(result));
    }

    @DeleteMapping("/{sedeId}/zonas/{zonaId}")
    public ResponseEntity<Void> eliminarZona(@PathVariable String sedeId, @PathVariable String zonaId) {
        eliminarZonaUseCase.execute(new EliminarZonaCommand(zonaId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{sedeId}")
    public ResponseEntity<Void> eliminarSede(@PathVariable String sedeId) {
        eliminarSedeUseCase.execute(new EliminarSedeCommand(sedeId));
        return ResponseEntity.noContent().build();
    }
}
