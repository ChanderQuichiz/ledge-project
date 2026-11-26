package com.ledgepay.catalogo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.ledgepay.catalogo.domain.exceptions.EventoPublicadoPendienteException;
import com.ledgepay.catalogo.domain.exceptions.HorasPreviasInvalidaException;
import com.ledgepay.catalogo.domain.exceptions.RangoFechaInvalidoException;
import com.ledgepay.catalogo.domain.value_objects.EstadoEvento;
import com.ledgepay.catalogo.domain.value_objects.PoliticaCancelacion;
import com.ledgepay.catalogo.domain.value_objects.RestriccionEdad;

class EventoTest {

    private static final UUID SEDE_ID = UUID.randomUUID();
    private static final UUID ORGANIZADOR_ID = UUID.randomUUID();
    private static final UUID CREADO_POR = UUID.randomUUID();

    @Test
    void registrar_debeCrearEventoEnBorrador_cuandoFechasSonValidas() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);
        LocalDateTime fin = inicio.plusDays(1);

        Evento evento = registrar(inicio, fin);

        assertThat(evento.getId()).isNotNull();
        assertThat(evento.getSedeId()).isEqualTo(SEDE_ID);
        assertThat(evento.getOrganizadorId()).isEqualTo(ORGANIZADOR_ID);
        assertThat(evento.getNombre()).isEqualTo("Evento");
        assertThat(evento.getDescripcion()).isEqualTo("Descripcion");
        assertThat(evento.getCategoria()).isEqualTo("Concierto");
        assertThat(evento.getEstado()).isEqualTo(EstadoEvento.BORRADOR);
        assertThat(evento.getNumeroReprogramaciones()).isZero();
        assertThat(evento.getRestriccionEdad()).isEqualTo(RestriccionEdad.MAYOR_DE_18);
        assertThat(evento.getPoliticaCancelacion()).isEqualTo(PoliticaCancelacion.ESTANDAR);
        assertThat(evento.getPermiteReingreso()).isTrue();
        assertThat(evento.getMaxReingresos()).isEqualTo(2);
        assertThat(evento.getEsNominativo()).isTrue();
        assertThat(evento.getEsExonerado()).isFalse();
        assertThat(evento.getLimiteCompraPorCliente()).isEqualTo(6);
        assertThat(evento.getFechaInicio()).isEqualTo(inicio);
        assertThat(evento.getFechaFin()).isEqualTo(fin);
        assertThat(evento.getAperturaPuertas()).isEqualTo(inicio.minusHours(2));
        assertThat(evento.getInicioVenta()).isEqualTo(inicio.minusDays(2));
        assertThat(evento.getFinVenta()).isEqualTo(fin.minusDays(2));
        assertThat(evento.getCreadoPor()).isEqualTo(CREADO_POR);
    }

    @Test
    void registrar_debeLanzarExcepcion_cuandoFaltanMenosDe72Horas() {
        LocalDateTime ahora = LocalDateTime.now();

        assertThatThrownBy(() -> registrar(ahora.plusHours(71), ahora.plusHours(72)))
            .isInstanceOf(HorasPreviasInvalidaException.class)
            .hasMessage("La fecha de inicio del evento debe ser al menos 72 horas después de la fecha de creación del evento.");
    }

    @Test
    void registrar_debeLanzarExcepcion_cuandoFechaFinNoEsPosteriorAlInicio() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);

        assertThatThrownBy(() -> registrar(inicio, inicio.minusDays(1)))
            .isInstanceOf(RangoFechaInvalidoException.class)
            .hasMessage("La fecha de fin del evento debe ser posterior a la fecha de inicio del evento.");
    }

    @Test
    void preventaVigente_debeSerTrue_cuandoFaltanMasDe72Horas() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);
        Evento evento = registrar(inicio, inicio.plusDays(1));

        assertThat(evento.preventaVigente()).isTrue();
    }

    @Test
    void preventaVigente_debeSerFalse_cuandoYaPasoElPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);
        Evento evento = registrar(inicio, inicio.plusDays(1));
        ReflectionTestUtils.setField(evento, "fechaInicio", LocalDateTime.now().minusDays(1));

        assertThat(evento.preventaVigente()).isFalse();
    }

    @Test
    void sinEventoPublicadoPendiente_noDebeLanzar_cuandoEventoEstaEnBorrador() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);
        Evento evento = registrar(inicio, inicio.plusDays(1));

        assertThatCode(evento::sinEventoPublicadoPendiente).doesNotThrowAnyException();
    }

    @Test
    void sinEventoPublicadoPendiente_debeLanzarExcepcion_cuandoEstaPublicadoYNoInicio() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(4);
        Evento evento = registrar(inicio, inicio.plusDays(1));
        ReflectionTestUtils.setField(evento, "estado", EstadoEvento.PUBLICADO);

        assertThatThrownBy(evento::sinEventoPublicadoPendiente)
            .isInstanceOf(EventoPublicadoPendienteException.class)
            .hasMessage("No se puede eliminar la zona porque el evento asociado está publicado o pendiente.");
    }

    private Evento registrar(LocalDateTime inicio, LocalDateTime fin) {
        return Evento.registrar(
            SEDE_ID,
            ORGANIZADOR_ID,
            "Evento",
            "Descripcion",
            "Concierto",
            RestriccionEdad.MAYOR_DE_18,
            PoliticaCancelacion.ESTANDAR,
            true,
            2,
            true,
            false,
            6,
            inicio,
            fin,
            inicio.minusHours(2),
            inicio.minusDays(2),
            fin.minusDays(2),
            CREADO_POR
        );
    }
}
