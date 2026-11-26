package com.ledgepay.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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

import com.ledgepay.catalogo.application.command.DesacticarSedeCommand;
import com.ledgepay.catalogo.application.exceptions.DesactivacionSedeInvalida;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.application.port.out.EntradasClient;
import com.ledgepay.catalogo.application.query.EntradasByEventoQuery;
import com.ledgepay.catalogo.application.result.DesactivarSedeResult;
import com.ledgepay.catalogo.application.result.EntradasByEventoResult;
import com.ledgepay.catalogo.domain.PuntoAccesoZona;
import com.ledgepay.catalogo.domain.PuntoAccesoSede;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.ZonaSede;
import com.ledgepay.catalogo.domain.repositories.PuntoAccesoZonaRepository;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;
import com.ledgepay.catalogo.domain.value_objects.TipoZona;

@ExtendWith(MockitoExtension.class)
class DesactivarSedeUseCaseTest {

    @Mock
    private EntradasClient entradasClient;

    @Mock
    private SedeRepository sedeRepository;

    @Mock
    private PuntoAccesoZonaRepository puntoAccesoZonaRepository;

    @Captor
    private ArgumentCaptor<EntradasByEventoQuery> queryCaptor;

    @Captor
    private ArgumentCaptor<Sede> sedeCaptor;

    @Mock
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void configurarTransaccion() {
        lenient().when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<DesactivarSedeResult> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
    }

    @Test
    void execute_debeDesactivarSede_cuandoNoHayEntradasAsociadas() {
        UUID sedeId = UUID.randomUUID();
        ZonaSede zona = ZonaSede.crear(sedeId, "Zona numerada", TipoZona.NUMERADA, 120);
        Sede sede = new Sede(sedeId, "Sede Lima", "Av. Principal 123", "Lima", 500, true,
            LocalDateTime.now(), List.of(zona), List.of());
        ReflectionTestUtils.setField(sede, "zonas", List.of(zona));
        ReflectionTestUtils.setField(sede, "puntosAcceso", List.of());

        when(entradasClient.getEntradasByEvento(any(EntradasByEventoQuery.class)))
            .thenReturn(new EntradasByEventoResult(List.of()));
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede));

        DesactivarSedeResult result = new DesactivarSedeUseCase(
            entradasClient, transactionTemplate, sedeRepository, puntoAccesoZonaRepository
        ).execute(new DesacticarSedeCommand(sedeId.toString()));

        assertThat(result.sedeId()).isEqualTo(sedeId.toString());
        assertThat(result.nombre()).isEqualTo("Sede Lima");
        assertThat(result.direccion()).isEqualTo("Av. Principal 123");
        assertThat(result.ciudad()).isEqualTo("Lima");
        assertThat(result.aforoMaximo()).isEqualTo(500);
        assertThat(result.estado()).isEqualTo("Inactiva");
        assertThat(result.zonas()).containsExactly(new DesactivarSedeResult.ZonaResult(
            zona.getId().toString(), "Zona numerada", "NUMERADA", 120, "Activa"
        ));
        assertThat(result.puntosAcceso()).isEmpty();

        verify(entradasClient).getEntradasByEvento(queryCaptor.capture());
        assertThat(queryCaptor.getValue().eventoId()).isEqualTo(sedeId.toString());

        verify(sedeRepository).save(sedeCaptor.capture());
        assertThat(sedeCaptor.getValue().getActiva()).isFalse();
    }

    @Test
    void execute_debeIncluirZonasDePuntosAcceso_cuandoHayPuntosAsociados() {
        UUID sedeId = UUID.randomUUID();
        UUID puntoAccesoId = UUID.randomUUID();
        UUID zonaId = UUID.randomUUID();
        PuntoAccesoSede puntoAcceso = new PuntoAccesoSede();
        ReflectionTestUtils.setField(puntoAcceso, "id", puntoAccesoId);
        ReflectionTestUtils.setField(puntoAcceso, "nombre", "Entrada principal");
        PuntoAccesoZona relacion = new PuntoAccesoZona();
        ReflectionTestUtils.setField(relacion, "puntoAccesoId", puntoAccesoId);
        ReflectionTestUtils.setField(relacion, "zonaId", zonaId);
        Sede sede = new Sede(sedeId, "Sede Lima", "Av. Principal 123", "Lima", 500, true,
            LocalDateTime.now(), List.of(), List.of(puntoAcceso));
        when(entradasClient.getEntradasByEvento(any(EntradasByEventoQuery.class)))
            .thenReturn(new EntradasByEventoResult(List.of()));
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede));
        when(puntoAccesoZonaRepository.findByPuntoAcessoSedeId(puntoAccesoId)).thenReturn(List.of(relacion));

        DesactivarSedeResult result = new DesactivarSedeUseCase(
            entradasClient, transactionTemplate, sedeRepository, puntoAccesoZonaRepository
        ).execute(new DesacticarSedeCommand(sedeId.toString()));

        assertThat(result.puntosAcceso()).hasSize(1);
        assertThat(result.puntosAcceso()[0].puntoAccesoId()).isEqualTo(puntoAccesoId.toString());
        assertThat(result.puntosAcceso()[0].nombre()).isEqualTo("Entrada principal");
        assertThat(result.puntosAcceso()[0].zonaIds()).containsExactly(zonaId.toString());
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoExistenEntradasAsociadas() {
        UUID sedeId = UUID.randomUUID();
        when(entradasClient.getEntradasByEvento(any(EntradasByEventoQuery.class)))
            .thenReturn(new EntradasByEventoResult(List.of(entrada())));

        assertThatThrownBy(() -> new DesactivarSedeUseCase(
            entradasClient, transactionTemplate, sedeRepository, puntoAccesoZonaRepository
        ).execute(new DesacticarSedeCommand(sedeId.toString())))
            .isInstanceOf(DesactivacionSedeInvalida.class)
            .hasMessage("No se puede desactivar la sede, ya que existen entradas asociadas a ella.");

        verifyNoInteractions(sedeRepository, puntoAccesoZonaRepository);
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoNoExisteLaSede() {
        UUID sedeId = UUID.randomUUID();
        when(entradasClient.getEntradasByEvento(any(EntradasByEventoQuery.class)))
            .thenReturn(new EntradasByEventoResult(List.of()));
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new DesactivarSedeUseCase(
            entradasClient, transactionTemplate, sedeRepository, puntoAccesoZonaRepository
        ).execute(new DesacticarSedeCommand(sedeId.toString())))
            .isInstanceOf(SedeNotFoundException.class)
            .hasMessage("No se encontró la sede con ID: " + sedeId);

        verify(sedeRepository, never()).save(any());
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
