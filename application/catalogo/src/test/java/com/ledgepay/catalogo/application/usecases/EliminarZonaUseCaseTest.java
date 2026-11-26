package com.ledgepay.catalogo.application.usecases;

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

import com.ledgepay.catalogo.application.command.EliminarZonaCommand;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.domain.Evento;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.ZonaSede;
import com.ledgepay.catalogo.domain.exceptions.EventoNotFoundException;
import com.ledgepay.catalogo.domain.exceptions.EventoPublicadoPendienteException;
import com.ledgepay.catalogo.domain.exceptions.ZonaNotFoundException;
import com.ledgepay.catalogo.domain.repositories.EventoRepository;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;
import com.ledgepay.catalogo.domain.repositories.ZonaSedeRepository;
import com.ledgepay.catalogo.domain.value_objects.EstadoEvento;
import com.ledgepay.catalogo.domain.value_objects.PoliticaCancelacion;
import com.ledgepay.catalogo.domain.value_objects.RestriccionEdad;
import com.ledgepay.catalogo.domain.value_objects.TipoZona;

@ExtendWith(MockitoExtension.class)
class EliminarZonaUseCaseTest {

    @Mock
    private ZonaSedeRepository zonaRepository;

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private SedeRepository sedeRepository;

    @Test
    void execute_debeEliminarZona_cuandoNoHayEventoPublicadoPendiente() {
        UUID sedeId = UUID.randomUUID();
        ZonaSede zona = ZonaSede.crear(sedeId, "Zona A", TipoZona.NUMERADA, 120);
        when(zonaRepository.findById(zona.getId())).thenReturn(Optional.of(zona));
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede(sedeId)));
        when(eventoRepository.findBySedeId(sedeId)).thenReturn(Optional.of(evento(sedeId)));

        new EliminarZonaUseCase(zonaRepository, eventoRepository, sedeRepository)
            .execute(new EliminarZonaCommand(zona.getId().toString()));

        verify(zonaRepository).delete(zona);
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoNoExisteLaZona() {
        UUID zonaId = UUID.randomUUID();
        when(zonaRepository.findById(zonaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new EliminarZonaUseCase(zonaRepository, eventoRepository, sedeRepository)
            .execute(new EliminarZonaCommand(zonaId.toString())))
            .isInstanceOf(ZonaNotFoundException.class)
            .hasMessage("No se encontró la zona con ID: " + zonaId);

        verifyNoInteractions(sedeRepository, eventoRepository);
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoNoExisteLaSede() {
        UUID sedeId = UUID.randomUUID();
        ZonaSede zona = ZonaSede.crear(sedeId, "Zona A", TipoZona.NUMERADA, 120);
        when(zonaRepository.findById(zona.getId())).thenReturn(Optional.of(zona));
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new EliminarZonaUseCase(zonaRepository, eventoRepository, sedeRepository)
            .execute(new EliminarZonaCommand(zona.getId().toString())))
            .isInstanceOf(SedeNotFoundException.class)
            .hasMessage("No se encontró la sede asociada a la zona con ID: " + sedeId);

        verifyNoInteractions(eventoRepository);
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoNoExisteEventoAsociado() {
        UUID sedeId = UUID.randomUUID();
        ZonaSede zona = ZonaSede.crear(sedeId, "Zona A", TipoZona.NUMERADA, 120);
        when(zonaRepository.findById(zona.getId())).thenReturn(Optional.of(zona));
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede(sedeId)));
        when(eventoRepository.findBySedeId(sedeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new EliminarZonaUseCase(zonaRepository, eventoRepository, sedeRepository)
            .execute(new EliminarZonaCommand(zona.getId().toString())))
            .isInstanceOf(EventoNotFoundException.class)
            .hasMessage("No se encontró un evento asociado a la sede con ID: " + sedeId);

        verify(zonaRepository, never()).delete(any());
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoEventoEstaPublicadoYPendiente() {
        UUID sedeId = UUID.randomUUID();
        ZonaSede zona = ZonaSede.crear(sedeId, "Zona A", TipoZona.NUMERADA, 120);
        Evento evento = evento(sedeId);
        ReflectionTestUtils.setField(evento, "estado", EstadoEvento.PUBLICADO);
        when(zonaRepository.findById(zona.getId())).thenReturn(Optional.of(zona));
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede(sedeId)));
        when(eventoRepository.findBySedeId(sedeId)).thenReturn(Optional.of(evento));

        assertThatThrownBy(() -> new EliminarZonaUseCase(zonaRepository, eventoRepository, sedeRepository)
            .execute(new EliminarZonaCommand(zona.getId().toString())))
            .isInstanceOf(EventoPublicadoPendienteException.class)
            .hasMessage("No se puede eliminar la zona porque el evento asociado está publicado o pendiente.");

        verify(zonaRepository, never()).delete(any());
    }

    private Sede sede(UUID sedeId) {
        return new Sede(sedeId, "Sede Lima", "Av. Principal 123", "Lima", 500, true,
            LocalDateTime.now(), java.util.List.of(), java.util.List.of());
    }

    private Evento evento(UUID sedeId) {
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);
        return Evento.registrar(
            sedeId,
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
