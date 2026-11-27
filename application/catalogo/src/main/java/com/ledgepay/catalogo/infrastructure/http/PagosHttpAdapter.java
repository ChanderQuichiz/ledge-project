package com.ledgepay.catalogo.infrastructure.http;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.ledgepay.catalogo.application.exceptions.PagosConsultaException;
import com.ledgepay.catalogo.application.port.out.PagosPort;

@Component
public class PagosHttpAdapter implements PagosPort {

    private final PagosHttpInterface pagosHttpInterface;

    public PagosHttpAdapter(PagosHttpInterface pagosHttpInterface) {
        this.pagosHttpInterface = pagosHttpInterface;
    }

    @Override
    public Boolean deudasVencidasPorOrganizador(UUID organizadorId) {
        try {
            return Boolean.TRUE.equals(pagosHttpInterface.getSituacionFinanciera(organizadorId).tieneDeudasVencidas());
        } catch (RestClientException exception) {
            throw new PagosConsultaException("No se pudo consultar la situación financiera del organizador", exception);
        }
    }
}
