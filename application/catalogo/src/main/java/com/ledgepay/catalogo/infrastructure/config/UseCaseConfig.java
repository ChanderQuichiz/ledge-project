package com.ledgepay.catalogo.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.ledgepay.catalogo.application.port.out.EntradasClient;
import com.ledgepay.catalogo.application.usecases.DefinirAsientoUseCase;
import com.ledgepay.catalogo.application.usecases.DesactivarSedeUseCase;
import com.ledgepay.catalogo.application.usecases.DesactivarZonaUseCase;
import com.ledgepay.catalogo.application.usecases.EliminarSedeUseCase;
import com.ledgepay.catalogo.application.usecases.EliminarZonaUseCase;
import com.ledgepay.catalogo.application.usecases.RecalificarAsientoUseCase;
import com.ledgepay.catalogo.application.usecases.RegisterEventoUseCase;
import com.ledgepay.catalogo.application.usecases.RegistrarSedeUseCase;
import com.ledgepay.catalogo.application.usecases.RegistrarZonaUseCase;
import com.ledgepay.catalogo.domain.repositories.EventoRepository;
import com.ledgepay.catalogo.domain.repositories.FaseVentaRepository;
import com.ledgepay.catalogo.domain.repositories.PuntoAccesoZonaRepository;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;
import com.ledgepay.catalogo.domain.repositories.ZonaSedeRepository;

@Configuration(proxyBeanMethods = false)
public class UseCaseConfig {

    @Bean
    TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    DefinirAsientoUseCase definirAsientoUseCase(ZonaSedeRepository zonaSedeRepository) {
        return new DefinirAsientoUseCase(zonaSedeRepository);
    }

    @Bean
    DesactivarSedeUseCase desactivarSedeUseCase(EntradasClient entradasClient, TransactionTemplate transactionTemplate,
            SedeRepository sedeRepository, PuntoAccesoZonaRepository puntoAccesoZonaRepository) {
        return new DesactivarSedeUseCase(entradasClient, transactionTemplate, sedeRepository,
            puntoAccesoZonaRepository);
    }

    @Bean
    DesactivarZonaUseCase desactivarZonaUseCase(SedeRepository sedeRepository, EntradasClient entradasClient,
            TransactionTemplate transactionTemplate) {
        return new DesactivarZonaUseCase(sedeRepository, entradasClient, transactionTemplate);
    }

    @Bean
    EliminarSedeUseCase eliminarSedeUseCase(SedeRepository sedeRepository, EventoRepository eventoRepository) {
        return new EliminarSedeUseCase(sedeRepository, eventoRepository);
    }

    @Bean
    EliminarZonaUseCase eliminarZonaUseCase(ZonaSedeRepository zonaSedeRepository,
            EventoRepository eventoRepository, SedeRepository sedeRepository) {
        return new EliminarZonaUseCase(zonaSedeRepository, eventoRepository, sedeRepository);
    }

    @Bean
    RecalificarAsientoUseCase recalificarAsientoUseCase(FaseVentaRepository faseVentaRepository,
            EventoRepository eventoRepository) {
        return new RecalificarAsientoUseCase(faseVentaRepository, eventoRepository);
    }

    @Bean
    RegisterEventoUseCase registerEventoUseCase(EventoRepository eventoRepository, SedeRepository sedeRepository) {
        return new RegisterEventoUseCase(eventoRepository, sedeRepository);
    }

    @Bean
    RegistrarSedeUseCase registrarSedeUseCase(SedeRepository sedeRepository) {
        return new RegistrarSedeUseCase(sedeRepository);
    }

    @Bean
    RegistrarZonaUseCase registrarZonaUseCase(SedeRepository sedeRepository) {
        return new RegistrarZonaUseCase(sedeRepository);
    }
}
