package com.ledgepay.catalogo.application.usecases;

import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.ledgepay.catalogo.application.command.EliminarZonaCommand;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.domain.Evento;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.ZonaSede;
import com.ledgepay.catalogo.domain.exceptions.EventoNotFoundException;
import com.ledgepay.catalogo.domain.exceptions.ZonaNotFoundException;
import com.ledgepay.catalogo.domain.repositories.EventoRepository;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;
import com.ledgepay.catalogo.domain.repositories.ZonaSedeRepository;

public class EliminarZonaUseCase {

private final ZonaSedeRepository zonaRepository;
private final EventoRepository eventoRepository;
private final SedeRepository sedeRepository;

public EliminarZonaUseCase(ZonaSedeRepository zonaRepository, EventoRepository eventoRepository, SedeRepository sedeRepository ) {
        this.zonaRepository = zonaRepository;
        this.eventoRepository = eventoRepository;
        this.sedeRepository = sedeRepository;
    }

    @Transactional
    public void execute(EliminarZonaCommand command) {
        // Lógica para eliminar la zona con el ID proporcionado en el comando.
        // Por ejemplo, podrías llamar a un repositorio para eliminar la zona de la base de datos.
    ZonaSede zona = this.zonaRepository.findById(UUID.fromString(command.zonaId())).orElseThrow(()-> new ZonaNotFoundException("No se encontró la zona con ID: " + command.zonaId()) );
    
        Sede sede = this.sedeRepository.findById(zona.getSedeId()).orElseThrow(() -> new SedeNotFoundException("No se encontró la sede asociada a la zona con ID: " + zona.getSedeId()));

        Evento evento = this.eventoRepository.findBySedeId(sede.getId()).orElseThrow(() -> new EventoNotFoundException("No se encontró un evento asociado a la sede con ID: " + sede.getId()));

        evento.sinEventoPublicadoPendiente();

        this.zonaRepository.delete(zona);
    
    }
}
