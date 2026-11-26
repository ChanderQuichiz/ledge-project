package com.ledgepay.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ledgepay.catalogo.application.command.DesactivarZonaCommand;
import com.ledgepay.catalogo.application.exceptions.DesactivacionSedeInvalida;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.application.port.out.EntradasClient;
import com.ledgepay.catalogo.application.query.EntradasByEventoQuery;
import com.ledgepay.catalogo.application.result.DesactivarZonaResult;
import com.ledgepay.catalogo.application.result.EntradasByEventoResult;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.ZonaSede;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;
import com.ledgepay.catalogo.domain.value_objects.TipoZona;

@ExtendWith(MockitoExtension.class)
class DesactivarZonaUseCaseTest {

    @Mock
    private SedeRepository sedeRepository;

    @Mock
    private EntradasClient entradasClient;

    @Captor
    private ArgumentCaptor<Sede> sedeCaptor;

    @Mock
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void configurarTransaccion() {
        lenient().when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<DesactivarZonaResult> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
    }

    @Test
    void execute_debeDesactivarZona_cuandoNoHayEntradasAsociadas() {
        UUID sedeId = UUID.randomUUID();
        ZonaSede zona = ZonaSede.crear(sedeId, "Zona A", TipoZona.NUMERADA, 120);
        Sede sede = sedeConZonas(sedeId, List.of(zona));
        when(entradasClient.getEntradasByEvento(any(EntradasByEventoQuery.class)))
            .thenReturn(new EntradasByEventoResult(List.of()));
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede));

        DesactivarZonaResult result = new DesactivarZonaUseCase(sedeRepository, entradasClient, transactionTemplate)
            .execute(new DesactivarZonaCommand(sedeId.toString(), zona.getId().toString()));

        assertThat(result.zonaId()).isEqualTo(zona.getId().toString());
        assertThat(result.nombre()).isEqualTo("Zona A");
        assertThat(result.tipo()).isEqualTo("NUMERADA");
        assertThat(result.capacidad()).isEqualTo(120);
        assertThat(result.estado()).isEqualTo("DESACTIVADA");

        verify(sedeRepository).save(sedeCaptor.capture());
        assertThat(sedeCaptor.getValue().getZonas().get(0).getActiva()).isFalse();
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoHayEntradasAsociadas() {
        UUID sedeId = UUID.randomUUID();
        when(entradasClient.getEntradasByEvento(any(EntradasByEventoQuery.class)))
            .thenReturn(new EntradasByEventoResult(List.of(entrada())));

        assertThatThrownBy(() -> new DesactivarZonaUseCase(sedeRepository, entradasClient, transactionTemplate)
            .execute(new DesactivarZonaCommand(sedeId.toString(), UUID.randomUUID().toString())))
            .isInstanceOf(DesactivacionSedeInvalida.class)
            .hasMessage("No se puede desactivar la sede, ya que existen entradas asociadas a ella.");

        verifyNoInteractions(sedeRepository);
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoNoExisteLaSede() {
        UUID sedeId = UUID.randomUUID();
        when(entradasClient.getEntradasByEvento(any(EntradasByEventoQuery.class)))
            .thenReturn(new EntradasByEventoResult(List.of()));
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new DesactivarZonaUseCase(sedeRepository, entradasClient, transactionTemplate)
            .execute(new DesactivarZonaCommand(sedeId.toString(), UUID.randomUUID().toString())))
            .isInstanceOf(SedeNotFoundException.class)
            .hasMessage("No se encontró la sede con ID: " + sedeId);
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoNoExisteLaZona() {
        UUID sedeId = UUID.randomUUID();
        UUID zonaId = UUID.randomUUID();
        Sede sede = sedeConZonas(sedeId, List.of());
        when(entradasClient.getEntradasByEvento(any(EntradasByEventoQuery.class)))
            .thenReturn(new EntradasByEventoResult(List.of()));
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede));

        assertThatThrownBy(() -> new DesactivarZonaUseCase(sedeRepository, entradasClient, transactionTemplate)
            .execute(new DesactivarZonaCommand(sedeId.toString(), zonaId.toString())))
            .isInstanceOf(SedeNotFoundException.class)
            .hasMessage("No se encontró la zona con ID: " + zonaId);
    }

    private Sede sedeConZonas(UUID sedeId, List<ZonaSede> zonas) {
        Sede sede = new Sede(sedeId, "Sede Lima", "Av. Principal 123", "Lima", 500, true,
            LocalDateTime.now(), List.of(), List.of());
        ReflectionTestUtils.setField(sede, "zonas", new ArrayList<>(zonas));
        return sede;
    }

    private EntradasByEventoResult.Entrada entrada() {
        return new EntradasByEventoResult.Entrada(
            "entrada-1", "COD-1", "reserva-1", null, null, null,
            1, 1, "VIGENTE", false,
            null, null, null, null, null,
            0, false, null, false,
            null, null, null, null
        );
    }
}
