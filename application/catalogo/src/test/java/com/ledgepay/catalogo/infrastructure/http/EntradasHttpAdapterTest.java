package com.ledgepay.catalogo.infrastructure.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import com.ledgepay.catalogo.application.exceptions.EntradasConsultaException;
import com.ledgepay.catalogo.application.query.EntradasByEventoQuery;
import com.ledgepay.catalogo.application.result.EntradasByEventoResult;

@ExtendWith(MockitoExtension.class)
class EntradasHttpAdapterTest {

    @Mock
    private EntradasHttpInterface entradasHttpInterface;

    @Test
    void getEntradasByEvento_debeDelegarAlClienteHttp() {
        UUID eventoId = UUID.randomUUID();
        EntradasByEventoResult esperado = new EntradasByEventoResult(List.of());
        when(entradasHttpInterface.getEntradasByEvento(eventoId)).thenReturn(esperado);

        EntradasByEventoResult resultado = new EntradasHttpAdapter(entradasHttpInterface)
            .getEntradasByEvento(new EntradasByEventoQuery(eventoId.toString()));

        assertThat(resultado).isSameAs(esperado);
        verify(entradasHttpInterface).getEntradasByEvento(eventoId);
    }

    @Test
    void getEntradasByEvento_debeMapearFalloHttp() {
        UUID eventoId = UUID.randomUUID();
        when(entradasHttpInterface.getEntradasByEvento(eventoId))
            .thenThrow(new RestClientException("fallo HTTP"));

        assertThatThrownBy(() -> new EntradasHttpAdapter(entradasHttpInterface)
            .getEntradasByEvento(new EntradasByEventoQuery(eventoId.toString())))
            .isInstanceOf(EntradasConsultaException.class)
            .hasMessage("No se pudo consultar las entradas del evento")
            .hasCauseInstanceOf(RestClientException.class);
    }
}
