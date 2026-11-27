package com.ledgepay.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ledgepay.catalogo.application.command.FechaEventoSuperpuestaException;
import com.ledgepay.catalogo.application.command.RegisterEventoCommand;
import com.ledgepay.catalogo.application.exceptions.DeudaVencidaException;
import com.ledgepay.catalogo.application.exceptions.OrganizadorNoVerificadoException;
import com.ledgepay.catalogo.application.exceptions.OrganizadorNotFoundException;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.application.port.out.PagosPort;
import com.ledgepay.catalogo.application.result.RegisterEventoResult;
import com.ledgepay.catalogo.domain.Evento;
import com.ledgepay.catalogo.domain.Organizador;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.ZonaSede;
import com.ledgepay.catalogo.domain.exceptions.AforoInsuficienteException;
import com.ledgepay.catalogo.domain.repositories.EventoRepository;
import com.ledgepay.catalogo.domain.repositories.OrganizadorRepository;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;
import com.ledgepay.catalogo.domain.value_objects.EstadoEvento;
import com.ledgepay.catalogo.domain.value_objects.EstadoOrganizador;
import com.ledgepay.catalogo.domain.value_objects.PoliticaCancelacion;
import com.ledgepay.catalogo.domain.value_objects.RestriccionEdad;
import com.ledgepay.catalogo.domain.value_objects.TipoZona;

@ExtendWith(MockitoExtension.class)
class RegisterEventoUseCaseTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private SedeRepository sedeRepository;

    @Mock
    private OrganizadorRepository organizadorRepository;

    @Mock
    private PagosPort pagosPort;

    @Captor
    private ArgumentCaptor<Evento> eventoCaptor;

    @Test
    void execute_debeRegistrarEvento_cuandoSedeTieneZonasYFechasSonValidas() {
        UUID sedeId = UUID.randomUUID();
        UUID organizadorId = UUID.randomUUID();
        UUID creadoPor = UUID.randomUUID();
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);
        LocalDateTime fin = inicio.plusDays(1);
        ZonaSede zona = ZonaSede.crear(sedeId, "Zona A", TipoZona.NUMERADA, 120);
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede(sedeId, List.of(zona))));
        stubsBase(sedeId, organizadorId);

        RegisterEventoResult result = useCase()
            .execute(command(sedeId, organizadorId, creadoPor, inicio, fin));

        verify(eventoRepository).save(eventoCaptor.capture());
        Evento evento = eventoCaptor.getValue();

        assertThat(result.eventoId()).isEqualTo(evento.getId().toString());
        assertThat(result.organizadorId()).isEqualTo(organizadorId.toString());
        assertThat(result.sede().sedeId()).isEqualTo(sedeId.toString());
        assertThat(result.sede().nombre()).isEqualTo("Sede Lima");
        assertThat(result.sede().ciudad()).isEqualTo("Lima");
        assertThat(result.zonas()).hasSize(1);
        assertThat(result.zonas().get(0).zonaId()).isEqualTo(zona.getId().toString());
        assertThat(result.zonas().get(0).nombre()).isEqualTo("Zona A");
        assertThat(result.zonas().get(0).tipo()).isEqualTo("NUMERADA");
        assertThat(result.zonas().get(0).capacidadOfrecida()).isEqualTo(120);
        assertThat(result.zonas().get(0).precioVigente()).isNull();
        assertThat(result.zonas().get(0).faseVigente()).isNull();
        assertThat(result.zonas().get(0).tarifaDinamica()).isFalse();
        assertThat(result.reprogramaciones()).isZero();
        assertThat(result.estado()).isEqualTo("BORRADOR");
        assertThat(result.nombre()).isEqualTo("Evento");
        assertThat(result.descripcion()).isEqualTo("Descripcion");
        assertThat(result.categoria()).isEqualTo("Concierto");
        assertThat(result.fechaInicio()).isEqualTo(inicio.toString());
        assertThat(result.fechaFin()).isEqualTo(fin.toString());
        assertThat(result.restriccionEdad()).isEqualTo("MAYOR_DE_14");
        assertThat(result.politicaCancelacion()).isEqualTo("ESTANDAR");
        assertThat(result.permiteReingreso()).isTrue();
        assertThat(result.maxReingresos()).isEqualTo(2);
        assertThat(result.esNominativo()).isFalse();
        assertThat(result.limiteCompraPorCliente()).isEqualTo(6);
        assertThat(result.eventoExonerado()).isFalse();
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoNoExisteLaSede() {
        UUID sedeId = UUID.randomUUID();
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.empty());
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);

        assertThatThrownBy(() -> useCase()
            .execute(command(sedeId, UUID.randomUUID(), UUID.randomUUID(), inicio, inicio.plusDays(1))))
            .isInstanceOf(SedeNotFoundException.class)
            .hasMessage("No se encontró la sede con ID: " + sedeId);

        verifyNoInteractions(eventoRepository);
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoSedeNoTieneZonas() {
        UUID sedeId = UUID.randomUUID();
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede(sedeId, List.of())));
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);

        assertThatThrownBy(() -> useCase()
            .execute(command(sedeId, UUID.randomUUID(), UUID.randomUUID(), inicio, inicio.plusDays(1))))
            .isInstanceOf(AforoInsuficienteException.class)
            .hasMessage("La sede debe tener al menos una zona para poder registrar un evento");

        verify(eventoRepository, never()).save(any());
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoNoExisteElOrganizador() {
        UUID sedeId = UUID.randomUUID();
        UUID organizadorId = UUID.randomUUID();
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);
        ZonaSede zona = ZonaSede.crear(sedeId, "Zona A", TipoZona.NUMERADA, 120);
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede(sedeId, List.of(zona))));
        when(organizadorRepository.findById(organizadorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase()
            .execute(command(sedeId, organizadorId, UUID.randomUUID(), inicio, inicio.plusDays(1))))
            .isInstanceOf(OrganizadorNotFoundException.class)
            .hasMessage("No se encontró el organizador con ID: " + organizadorId);

        verifyNoInteractions(pagosPort, eventoRepository);
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoOrganizadorNoEstaVerificado() {
        UUID sedeId = UUID.randomUUID();
        UUID organizadorId = UUID.randomUUID();
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);
        ZonaSede zona = ZonaSede.crear(sedeId, "Zona A", TipoZona.NUMERADA, 120);
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede(sedeId, List.of(zona))));
        when(organizadorRepository.findById(organizadorId))
            .thenReturn(Optional.of(organizador(organizadorId, EstadoOrganizador.PENDIENTE_VERIFICACION)));

        assertThatThrownBy(() -> useCase()
            .execute(command(sedeId, organizadorId, UUID.randomUUID(), inicio, inicio.plusDays(1))))
            .isInstanceOf(OrganizadorNoVerificadoException.class)
            .hasMessage("El organizador no está verificado y no puede registrar eventos");

        verifyNoInteractions(pagosPort, eventoRepository);
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoOrganizadorTieneDeudas() {
        UUID sedeId = UUID.randomUUID();
        UUID organizadorId = UUID.randomUUID();
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);
        ZonaSede zona = ZonaSede.crear(sedeId, "Zona A", TipoZona.NUMERADA, 120);
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede(sedeId, List.of(zona))));
        when(organizadorRepository.findById(organizadorId))
            .thenReturn(Optional.of(organizador(organizadorId, EstadoOrganizador.VERIFICADO)));
        when(pagosPort.deudasVencidasPorOrganizador(organizadorId)).thenReturn(true);

        assertThatThrownBy(() -> useCase()
            .execute(command(sedeId, organizadorId, UUID.randomUUID(), inicio, inicio.plusDays(1))))
            .isInstanceOf(DeudaVencidaException.class)
            .hasMessage("El organizador tiene deuda vencida y no puede registrar un nuevo evento");

        verifyNoInteractions(eventoRepository);
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoFechasSeSuperponen() {
        UUID sedeId = UUID.randomUUID();
        UUID organizadorId = UUID.randomUUID();
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);
        LocalDateTime fin = inicio.plusDays(1);
        ZonaSede zona = ZonaSede.crear(sedeId, "Zona A", TipoZona.NUMERADA, 120);
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede(sedeId, List.of(zona))));
        stubsBase(sedeId, organizadorId);
        when(eventoRepository.findAllBySedeId(sedeId))
            .thenReturn(List.of(evento(sedeId, inicio.plusDays(1), inicio.plusDays(2), EstadoEvento.PUBLICADO)));

        assertThatThrownBy(() -> useCase()
            .execute(command(sedeId, organizadorId, UUID.randomUUID(), inicio, fin)))
            .isInstanceOf(FechaEventoSuperpuestaException.class)
            .hasMessage("La fecha del evento se superpone con otro evento en la misma sede.");

        verify(eventoRepository, never()).save(any());
    }

    private RegisterEventoUseCase useCase() {
        return new RegisterEventoUseCase(eventoRepository, sedeRepository, organizadorRepository, pagosPort);
    }

    private void stubsBase(UUID sedeId, UUID organizadorId) {
        when(organizadorRepository.findById(organizadorId))
            .thenReturn(Optional.of(organizador(organizadorId, EstadoOrganizador.VERIFICADO)));
        when(pagosPort.deudasVencidasPorOrganizador(organizadorId)).thenReturn(false);
        when(eventoRepository.findAllBySedeId(sedeId)).thenReturn(List.of());
    }

    private Sede sede(UUID sedeId, List<ZonaSede> zonas) {
        Sede sede = new Sede(sedeId, "Sede Lima", "Av. Principal 123", "Lima", 500, true,
            LocalDateTime.now(), List.of(), List.of());
        ReflectionTestUtils.setField(sede, "zonas", new ArrayList<>(zonas));
        return sede;
    }

    private Organizador organizador(UUID organizadorId, EstadoOrganizador estado) {
        LocalDateTime ahora = LocalDateTime.now();
        return new Organizador(organizadorId, "12345678901", "Organizador", estado,
            new BigDecimal("5.00"), UUID.randomUUID(), ahora, ahora, ahora);
    }

    private Evento evento(UUID sedeId, LocalDateTime inicio, LocalDateTime fin, EstadoEvento estado) {
        Evento evento = Evento.registrar(
            sedeId,
            UUID.randomUUID(),
            "Evento existente",
            "Descripcion",
            "Concierto",
            RestriccionEdad.MAYOR_DE_14,
            PoliticaCancelacion.ESTANDAR,
            true,
            2,
            false,
            false,
            6,
            inicio,
            fin,
            inicio.minusMinutes(30),
            inicio.minusDays(2),
            inicio.minusDays(1),
            UUID.randomUUID()
        );
        ReflectionTestUtils.setField(evento, "estado", estado);
        return evento;
    }

    private RegisterEventoCommand command(UUID sedeId, UUID organizadorId, UUID creadoPor,
                                          LocalDateTime inicio, LocalDateTime fin) {
        return new RegisterEventoCommand(
            sedeId.toString(),
            organizadorId.toString(),
            "Evento",
            "Descripcion",
            "Concierto",
            inicio.toString(),
            fin.toString(),
            inicio.minusMinutes(30).toString(),
            "MAYOR_DE_14",
            "ESTANDAR",
            true,
            2,
            false,
            6,
            false,
            inicio.minusDays(2).toString(),
            inicio.minusDays(1).toString(),
            creadoPor.toString()
        );
    }
}
