package com.ledgepay.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ledgepay.catalogo.application.command.DefinirAsientoCommand;
import com.ledgepay.catalogo.application.result.DefinirAsientoResult;
import com.ledgepay.catalogo.domain.ZonaSede;
import com.ledgepay.catalogo.domain.exceptions.AsientosDuplicadosException;
import com.ledgepay.catalogo.domain.exceptions.ZonaNotFoundException;
import com.ledgepay.catalogo.domain.repositories.ZonaSedeRepository;
import com.ledgepay.catalogo.domain.value_objects.TipoZona;

@ExtendWith(MockitoExtension.class)
class DefinirAsientoUseCaseTest {

    @Mock
    private ZonaSedeRepository zonaSedeRepository;

    @Test
    void execute_debeDefinirAsientos_cuandoZonaExiste() {
        UUID sedeId = UUID.randomUUID();
        ZonaSede zona = ZonaSede.crear(sedeId, "Zona A", TipoZona.NUMERADA, 120);
        when(zonaSedeRepository.findById(zona.getId())).thenReturn(Optional.of(zona));
        DefinirAsientoCommand command = new DefinirAsientoCommand(
            sedeId.toString(),
            zona.getId().toString(),
            List.of(
                new DefinirAsientoCommand.Asiento("A", 1, true, false),
                new DefinirAsientoCommand.Asiento("B", 2, true, true)
            )
        );

        DefinirAsientoResult result = new DefinirAsientoUseCase(zonaSedeRepository).execute(command);

        verify(zonaSedeRepository).save(zona);
        assertThat(result.zonaId()).isEqualTo(zona.getId().toString());
        assertThat(result.total()).isEqualTo(2);
        assertThat(zona.getAsientos())
            .extracting("fila", "numero", "habilitado", "accesible")
            .containsExactly(
                org.assertj.core.groups.Tuple.tuple("A", 1, true, false),
                org.assertj.core.groups.Tuple.tuple("B", 2, true, true)
            );
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoNoExisteLaZona() {
        UUID zonaId = UUID.randomUUID();
        when(zonaSedeRepository.findById(zonaId)).thenReturn(Optional.empty());
        DefinirAsientoCommand command = new DefinirAsientoCommand(
            UUID.randomUUID().toString(),
            zonaId.toString(),
            List.of(new DefinirAsientoCommand.Asiento("A", 1, true, false))
        );

        assertThatThrownBy(() -> new DefinirAsientoUseCase(zonaSedeRepository).execute(command))
            .isExactlyInstanceOf(ZonaNotFoundException.class)
            .hasMessage("No se encontró la zona con ID: " + zonaId);

        verify(zonaSedeRepository).findById(zonaId);
        verify(zonaSedeRepository, never()).save(any());
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoAsientosEstanDuplicados() {
        UUID sedeId = UUID.randomUUID();
        ZonaSede zona = ZonaSede.crear(sedeId, "Zona A", TipoZona.NUMERADA, 120);
        when(zonaSedeRepository.findById(zona.getId())).thenReturn(Optional.of(zona));
        DefinirAsientoCommand command = new DefinirAsientoCommand(
            sedeId.toString(),
            zona.getId().toString(),
            List.of(
                new DefinirAsientoCommand.Asiento("A", 1, true, false),
                new DefinirAsientoCommand.Asiento("A", 1, true, false)
            )
        );

        assertThatThrownBy(() -> new DefinirAsientoUseCase(zonaSedeRepository).execute(command))
            .isInstanceOf(AsientosDuplicadosException.class)
            .hasMessage("La zona tiene asientos duplicados: A-1");

        verify(zonaSedeRepository, never()).save(any());
    }
}
