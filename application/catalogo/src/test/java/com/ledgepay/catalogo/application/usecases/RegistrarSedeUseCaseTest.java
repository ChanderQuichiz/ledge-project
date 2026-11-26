package com.ledgepay.catalogo.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ledgepay.catalogo.application.command.RegistrarSedeCommand;
import com.ledgepay.catalogo.application.result.RegistrarSedeResult;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.exceptions.AforoInsuficienteException;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;

@ExtendWith(MockitoExtension.class)
class RegistrarSedeUseCaseTest {

    @Mock
    private SedeRepository sedeRepository;

    @Captor
    private ArgumentCaptor<Sede> sedeCaptor;

    @Test
    void execute_debeRegistrarSede_cuandoAforoEsValido() {
        when(sedeRepository.save(any(Sede.class))).thenAnswer(invocation -> invocation.getArgument(0));
        RegistrarSedeCommand command = new RegistrarSedeCommand("Sede Lima", "Av. Principal 123", "Lima", 500);

        RegistrarSedeResult result = new RegistrarSedeUseCase(sedeRepository).execute(command);

        verify(sedeRepository).save(sedeCaptor.capture());
        assertThat(result.sedeId()).isEqualTo(sedeCaptor.getValue().getId().toString());
        assertThat(result.nombre()).isEqualTo("Sede Lima");
        assertThat(result.direccion()).isEqualTo("Av. Principal 123");
        assertThat(result.ciudad()).isEqualTo("Lima");
        assertThat(result.aforoMaximo()).isEqualTo(500);
        assertThat(result.estado()).isEqualTo("Activa");
    }

    @Test
    void execute_debeLanzarExcepcion_cuandoAforoNoEsValido() {
        RegistrarSedeCommand command = new RegistrarSedeCommand("Sede Lima", "Av. Principal 123", "Lima", 0);

        assertThatThrownBy(() -> new RegistrarSedeUseCase(sedeRepository).execute(command))
            .isInstanceOf(AforoInsuficienteException.class)
            .hasMessage("Aforo máximo legal debe ser mayor a cero");

        verifyNoInteractions(sedeRepository);
    }
}
