package com.ledgepay.catalogo.infrastructure.http;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.ledgepay.catalogo.application.result.SituacionFinancieraOrganizadorResult;

@HttpExchange("/internal/organizadores")
public interface PagosHttpInterface {

    @GetExchange("/{organizadorId}/situacion-financiera")
    SituacionFinancieraOrganizadorResult getSituacionFinanciera(@PathVariable("organizadorId") UUID organizadorId);
}
