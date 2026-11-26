package com.ledgepay.catalogo.infrastructure.http;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.ledgepay.catalogo.application.exceptions.EntradasConsultaException;
import com.ledgepay.catalogo.application.port.out.EntradasClient;
import com.ledgepay.catalogo.application.query.EntradasByEventoQuery;
import com.ledgepay.catalogo.application.result.EntradasByEventoResult;

@Component
public class EntradasHttpAdapter implements EntradasClient {

    private final EntradasHttpInterface entradasHttpInterface;

    public EntradasHttpAdapter(EntradasHttpInterface entradasHttpInterface) {
        this.entradasHttpInterface = entradasHttpInterface;
    }

    @Override
    public EntradasByEventoResult getEntradasByEvento(EntradasByEventoQuery query) {
        try {
            return entradasHttpInterface.getEntradasByEvento(UUID.fromString(query.eventoId()));
        } catch (RestClientException exception) {
            throw new EntradasConsultaException("No se pudo consultar las entradas del evento", exception);
        }
    }
}
