package com.ledgepay.catalogo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.ledgepay.catalogo.domain.exceptions.AforoInsuficienteException;
import com.ledgepay.catalogo.domain.exceptions.ZonaDuplicadaException;
import com.ledgepay.catalogo.domain.value_objects.TipoZona;

class SedeTest {

    @Test
    void registrarSede_debeCrearSedeActiva_cuandoAforoEsValido() {
        LocalDateTime antes = LocalDateTime.now();

        Sede sede = Sede.RegisterSede("Sede Lima", "Av. Principal 123", "Lima", 500);

        LocalDateTime despues = LocalDateTime.now();
        assertThat(sede.getId()).isNotNull();
        assertThat(sede.getNombre()).isEqualTo("Sede Lima");
        assertThat(sede.getDireccion()).isEqualTo("Av. Principal 123");
        assertThat(sede.getCiudad()).isEqualTo("Lima");
        assertThat(sede.getAforoMaximoLegal()).isEqualTo(500);
        assertThat(sede.getActiva()).isTrue();
        assertThat(sede.getCreadoEn()).isBetween(antes, despues);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void registrarSede_debeLanzarExcepcion_cuandoAforoNoEsPositivo(int aforo) {
        assertThatThrownBy(() -> Sede.RegisterSede("Sede Lima", "Av. Principal 123", "Lima", aforo))
            .isInstanceOf(AforoInsuficienteException.class)
            .hasMessage("Aforo máximo legal debe ser mayor a cero");
    }

    @Test
    void registrarZona_debeAgregarZona_cuandoHayCapacidadDisponible() {
        Sede sede = sedeConZonas(500, List.of());
        ZonaSede zona = ZonaSede.crear(sede.getId(), "Zona A", TipoZona.NUMERADA, 120);

        Sede resultado = sede.RegistrarZona(zona);

        assertThat(resultado).isSameAs(sede);
        assertThat(sede.getZonas()).containsExactly(zona);
    }

    @Test
    void registrarZona_debeAceptarZona_cuandoCapacidadIgualaAforo() {
        ZonaSede existente = ZonaSede.crear(UUID.randomUUID(), "Zona A", TipoZona.NUMERADA, 40);
        Sede sede = sedeConZonas(100, List.of(existente));
        ZonaSede nueva = ZonaSede.crear(sede.getId(), "Zona B", TipoZona.GENERAL, 60);

        sede.RegistrarZona(nueva);

        assertThat(sede.getZonas()).containsExactly(existente, nueva);
    }

    @Test
    void registrarZona_debeLanzarExcepcion_cuandoExcedeAforo() {
        ZonaSede existente = ZonaSede.crear(UUID.randomUUID(), "Zona A", TipoZona.NUMERADA, 80);
        Sede sede = sedeConZonas(100, List.of(existente));
        ZonaSede nueva = ZonaSede.crear(sede.getId(), "Zona B", TipoZona.GENERAL, 50);

        assertThatThrownBy(() -> sede.RegistrarZona(nueva))
            .isInstanceOf(AforoInsuficienteException.class)
            .hasMessage("No hay capacidad disponible para registrar la zona");
    }

    @Test
    void registrarZona_debeLanzarExcepcion_cuandoZonaYaExiste() {
        ZonaSede existente = ZonaSede.crear(UUID.randomUUID(), "Zona A", TipoZona.NUMERADA, 80);
        Sede sede = sedeConZonas(500, List.of(existente));
        ZonaSede repetida = ZonaSede.crear(sede.getId(), "Zona A", TipoZona.GENERAL, 50);

        assertThatThrownBy(() -> sede.RegistrarZona(repetida))
            .isInstanceOf(ZonaDuplicadaException.class)
            .hasMessage("Ya existe una zona con el mismo nombre");
    }

    @Test
    void desactivar_debeMarcarSedeInactiva() {
        Sede sede = sedeConZonas(500, List.of());

        sede.desactivar();

        assertThat(sede.getActiva()).isFalse();
    }

    @Test
    void zonasMinimasParaEvento_noDebeLanzar_cuandoHayUnaZona() {
        ZonaSede zona = ZonaSede.crear(UUID.randomUUID(), "Zona A", TipoZona.NUMERADA, 80);
        Sede sede = sedeConZonas(500, List.of(zona));

        assertThatCode(sede::zonasMinimasParaEvento).doesNotThrowAnyException();
    }

    @Test
    void zonasMinimasParaEvento_debeLanzarExcepcion_cuandoNoHayZonas() {
        Sede sede = sedeConZonas(500, List.of());

        assertThatThrownBy(sede::zonasMinimasParaEvento)
            .isInstanceOf(AforoInsuficienteException.class)
            .hasMessage("La sede debe tener al menos una zona para poder registrar un evento");
    }

    private Sede sedeConZonas(Integer aforo, List<ZonaSede> zonas) {
        Sede sede = new Sede(UUID.randomUUID(), "Sede Lima", "Av. Principal 123", "Lima", aforo,
            true, LocalDateTime.now(), List.of(), List.of());
        ReflectionTestUtils.setField(sede, "zonas", new ArrayList<>(zonas));
        return sede;
    }
}
