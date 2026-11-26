package com.ledgepay.catalogo.infrastructure.http;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.ledgepay.catalogo.application.result.EntradasByEventoResult;

@HttpExchange("/internal/eventos")
public interface EntradasHttpInterface {

    @GetExchange("/{eventoId}/entradas")
    EntradasByEventoResult getEntradasByEvento(@PathVariable("eventoId") UUID eventoId);
}
