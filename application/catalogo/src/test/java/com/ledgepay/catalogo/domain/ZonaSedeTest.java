package com.ledgepay.catalogo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.ledgepay.catalogo.domain.exceptions.AsientosDuplicadosException;
import com.ledgepay.catalogo.domain.value_objects.TipoZona;

class ZonaSedeTest {

    @Test
    void crear_debeInicializarZonaActiva() {
        UUID sedeId = UUID.randomUUID();

        ZonaSede zona = ZonaSede.crear(sedeId, "Zona A", TipoZona.NUMERADA, 120);

        assertThat(zona.getId()).isNotNull();
        assertThat(zona.getSedeId()).isEqualTo(sedeId);
        assertThat(zona.getNombre()).isEqualTo("Zona A");
        assertThat(zona.getTipo()).isEqualTo(TipoZona.NUMERADA);
        assertThat(zona.getCapacidad()).isEqualTo(120);
        assertThat(zona.getActiva()).isTrue();
    }

    @Test
    void desactivar_debeMarcarZonaInactiva() {
        ZonaSede zona = ZonaSede.crear(UUID.randomUUID(), "Zona A", TipoZona.NUMERADA, 120);

        zona.desactivar();

        assertThat(zona.getActiva()).isFalse();
    }

    @Test
    void definirAsientos_debeGuardarAsientos_cuandoSonUnicos() {
        ZonaSede zona = ZonaSede.crear(UUID.randomUUID(), "Zona A", TipoZona.NUMERADA, 120);
        AsientoZona primero = AsientoZona.crear(zona.getId(), "A", 1, false, true);
        AsientoZona segundo = AsientoZona.crear(zona.getId(), "A", 2, true, true);

        zona.definirAsientos(List.of(primero, segundo));

        assertThat(zona.getAsientos()).containsExactly(primero, segundo);
    }

    @Test
    void definirAsientos_debeLanzarExcepcion_cuandoHayAsientosDuplicados() {
        ZonaSede zona = ZonaSede.crear(UUID.randomUUID(), "Zona A", TipoZona.NUMERADA, 120);
        AsientoZona primero = AsientoZona.crear(zona.getId(), "A", 1, false, true);
        AsientoZona duplicado = AsientoZona.crear(zona.getId(), "A", 1, true, true);

        assertThatThrownBy(() -> zona.definirAsientos(List.of(primero, duplicado)))
            .isInstanceOf(AsientosDuplicadosException.class)
            .hasMessage("La zona tiene asientos duplicados: A-1");
    }

    @Test
    void unicidadDeAsientos_noDebeLanzar_cuandoAsientosSonUnicos() {
        ZonaSede zona = ZonaSede.crear(UUID.randomUUID(), "Zona A", TipoZona.NUMERADA, 120);
        ReflectionTestUtils.setField(zona, "asientos", List.of(
            AsientoZona.crear(zona.getId(), "A", 1, false, true),
            AsientoZona.crear(zona.getId(), "B", 1, true, true)
        ));

        assertThatCode(zona::unicidadDeAsientos).doesNotThrowAnyException();
    }

    @Test
    void unicidadDeAsientos_debeLanzarExcepcion_cuandoAsientosDuplicados() {
        ZonaSede zona = ZonaSede.crear(UUID.randomUUID(), "Zona A", TipoZona.NUMERADA, 120);
        ReflectionTestUtils.setField(zona, "asientos", List.of(
            AsientoZona.crear(zona.getId(), "A", 1, false, true),
            AsientoZona.crear(zona.getId(), "A", 1, true, true)
        ));

        assertThatThrownBy(zona::unicidadDeAsientos)
            .isInstanceOf(AsientosDuplicadosException.class)
            .hasMessage("La zona tiene asientos duplicados: A-1");
    }
}
