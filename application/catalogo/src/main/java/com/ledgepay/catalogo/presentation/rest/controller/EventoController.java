package com.ledgepay.catalogo.presentation.rest.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ledgepay.catalogo.application.command.RegisterEventoCommand;
import com.ledgepay.catalogo.application.result.RegisterEventoResult;
import com.ledgepay.catalogo.application.usecases.RegisterEventoUseCase;
import com.ledgepay.catalogo.presentation.rest.dto.EventoCrearRequest;
import com.ledgepay.catalogo.presentation.rest.dto.EventoResponse;
import com.ledgepay.catalogo.presentation.rest.mapper.DtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/eventos")
public class EventoController {

    private final RegisterEventoUseCase registerEventoUseCase;

    public EventoController(RegisterEventoUseCase registerEventoUseCase) {
        this.registerEventoUseCase = registerEventoUseCase;
    }

    @PostMapping
    public ResponseEntity<EventoResponse> crearEvento(@Valid @RequestBody EventoCrearRequest request) {
        RegisterEventoCommand command = DtoMapper.toCommand(request);
        RegisterEventoResult result = registerEventoUseCase.execute(command);
        EventoResponse response = DtoMapper.toResponse(result);
        return ResponseEntity.created(URI.create("/api/v1/eventos/" + response.eventoId())).body(response);
    }
}
