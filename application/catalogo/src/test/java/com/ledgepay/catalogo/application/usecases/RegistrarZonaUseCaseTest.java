package com.ledgepay.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ledgepay.catalogo.application.command.RegistrarZonaCommand;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.application.result.RegistrarZonaResult;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.ZonaSede;
import com.ledgepay.catalogo.domain.exceptions.AforoInsuficienteException;
import com.ledgepay.catalogo.domain.exceptions.ZonaDuplicadaException;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;
import com.ledgepay.catalogo.domain.value_objects.TipoZona;

@ExtendWith(MockitoExtension.class)
class RegistrarZonaUseCaseTest {

    @Mock
    private SedeRepository sedeRepository;

    @Test
    void execute_debeRegistrarZona_cuandoSedeExiste() {
        UUID sedeId = UUID.randomUUID();
        Sede sede = sede(sedeId, 500, List.of());
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede));

        RegistrarZonaResult result = new RegistrarZonaUseCase(sedeRepository)
            .execute(new RegistrarZonaCommand(sedeId.toString(), "Zona A", "NUMERADA", 120));

        assertThat(result.zonaId()).isNotBlank();
        assertThat(result.nombre()).isEqualTo("Zona A");
        assertThat(result.tipo()).isEqualTo("NUMERADA");
        assertThat(result.capacidad()).isEqualTo(120);
        assertThat(result.estado()).isEqualTo("ACTIVA");
        assertThat(sede.getZonas()).extracting(ZonaSede::getNombre).containsExactly("Zona A");
        verify(sedeRepository).save(sede);
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoNoExisteLaSede() {
        UUID sedeId = UUID.randomUUID();
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new RegistrarZonaUseCase(sedeRepository)
            .execute(new RegistrarZonaCommand(sedeId.toString(), "Zona A", "NUMERADA", 120)))
            .isInstanceOf(SedeNotFoundException.class)
            .hasMessage("Sede not found with id: " + sedeId);

        verify(sedeRepository, never()).save(any());
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoCapacidadSuperaAforo() {
        UUID sedeId = UUID.randomUUID();
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede(sedeId, 100, List.of())));

        assertThatThrownBy(() -> new RegistrarZonaUseCase(sedeRepository)
            .execute(new RegistrarZonaCommand(sedeId.toString(), "Zona A", "NUMERADA", 200)))
            .isInstanceOf(AforoInsuficienteException.class)
            .hasMessage("No hay capacidad disponible para registrar la zona");

        verify(sedeRepository, never()).save(any());
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoZonaYaExiste() {
        UUID sedeId = UUID.randomUUID();
        List<ZonaSede> zonas = List.of(ZonaSede.crear(sedeId, "Zona A", TipoZona.NUMERADA, 50));
        when(sedeRepository.findById(sedeId)).thenReturn(Optional.of(sede(sedeId, 500, zonas)));

        assertThatThrownBy(() -> new RegistrarZonaUseCase(sedeRepository)
            .execute(new RegistrarZonaCommand(sedeId.toString(), "Zona A", "NUMERADA", 50)))
            .isInstanceOf(ZonaDuplicadaException.class)
            .hasMessage("Ya existe una zona con el mismo nombre");

        verify(sedeRepository, never()).save(any());
    }

    private Sede sede(UUID sedeId, Integer aforo, List<ZonaSede> zonas) {
        Sede sede = new Sede(sedeId, "Sede Lima", "Av. Principal 123", "Lima", aforo, true,
            LocalDateTime.now(), List.of(), List.of());
        ReflectionTestUtils.setField(sede, "zonas", new ArrayList<>(zonas));
        return sede;
    }
}
