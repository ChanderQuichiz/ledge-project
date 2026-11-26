package com.ledgepay.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ledgepay.catalogo.application.command.RecalificarAsientoCommand;
import com.ledgepay.catalogo.application.exceptions.PreventaVigenteException;
import com.ledgepay.catalogo.domain.Evento;
import com.ledgepay.catalogo.domain.FaseVenta;
import com.ledgepay.catalogo.domain.repositories.EventoRepository;
import com.ledgepay.catalogo.domain.repositories.FaseVentaRepository;
import com.ledgepay.catalogo.domain.value_objects.Fase;
import com.ledgepay.catalogo.domain.value_objects.PoliticaCancelacion;
import com.ledgepay.catalogo.domain.value_objects.RestriccionEdad;

@ExtendWith(MockitoExtension.class)
class RecalificarAsientoUseCaseTest {

    @Mock
    private FaseVentaRepository faseVentaRepository;

    @Mock
    private EventoRepository eventoRepository;

    @Test
    void execute_debeIniciarVentaGeneral_cuandoPreventaYaNoVigente() {
        UUID eventoId = UUID.randomUUID();
        Evento evento = evento(eventoId);
        ReflectionTestUtils.setField(evento, "fechaInicio", LocalDateTime.now().plusDays(2));
        FaseVenta faseVenta = new FaseVenta();
        ReflectionTestUtils.setField(faseVenta, "fase", Fase.PREVENTA);
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
        when(faseVentaRepository.findByEventoId(evento.getId())).thenReturn(faseVenta);

        RecalificarAsientoUseCase useCase = new RecalificarAsientoUseCase(faseVentaRepository, eventoRepository);

        assertThatCode(() -> useCase.execute(new RecalificarAsientoCommand(eventoId.toString())))
            .doesNotThrowAnyException();

        verify(eventoRepository).findById(eventoId);
        verify(faseVentaRepository).findByEventoId(evento.getId());
        verify(faseVentaRepository).save(faseVenta);
        assertThat(faseVenta.getFase()).isEqualTo(Fase.GENERAL);
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoPreventaVigente() {
        UUID eventoId = UUID.randomUUID();
        Evento evento = evento(eventoId);
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));

        assertThatThrownBy(() -> new RecalificarAsientoUseCase(faseVentaRepository, eventoRepository)
            .execute(new RecalificarAsientoCommand(eventoId.toString())))
            .isInstanceOf(PreventaVigenteException.class)
            .hasMessage("No se puede recalificar asientos mientras la preventa esté vigente");

        verifyNoInteractions(faseVentaRepository);
        verify(faseVentaRepository, never()).save(any());
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoNoExisteEvento() {
        UUID eventoId = UUID.randomUUID();
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new RecalificarAsientoUseCase(faseVentaRepository, eventoRepository)
            .execute(new RecalificarAsientoCommand(eventoId.toString())))
            .isExactlyInstanceOf(RuntimeException.class)
            .hasMessage("Evento no encontrado");

        verifyNoInteractions(faseVentaRepository);
    }

    private Evento evento(UUID eventoId) {
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);
        return Evento.registrar(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Evento",
            "Descripcion",
            "Concierto",
            RestriccionEdad.TODO_PUBLICO,
            PoliticaCancelacion.ESTANDAR,
            true,
            2,
            true,
            false,
            6,
            inicio,
            inicio.plusDays(1),
            null,
            inicio.minusDays(2),
            inicio.minusDays(1),
            UUID.randomUUID()
        );
    }
}
