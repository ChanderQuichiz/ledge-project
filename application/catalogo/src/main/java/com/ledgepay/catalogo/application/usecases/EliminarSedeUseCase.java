package com.ledgepay.catalogo.application.usecases;

import java.util.UUID;

import com.ledgepay.catalogo.application.command.EliminarSedeCommand;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.domain.Evento;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.exceptions.EventoNotFoundException;
import com.ledgepay.catalogo.domain.repositories.EventoRepository;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;


public class EliminarSedeUseCase {

    private final SedeRepository sedeRepository;
    private final EventoRepository eventoRepository;
    public EliminarSedeUseCase(SedeRepository sedeRepository, EventoRepository eventoRepository) {
        this.sedeRepository = sedeRepository;
        this.eventoRepository = eventoRepository;
    }
    public void execute(EliminarSedeCommand command) {
        // Lógica para eliminar la sede con el ID proporcionado en el comando
        // Por ejemplo, podrías llamar a un repositorio para eliminar la sede de la base de datos
    Sede sede = this.sedeRepository.findById(UUID.fromString(command.sedeId())).orElseThrow(()-> new SedeNotFoundException("No se encontró la sede con ID: " + command.sedeId()) );
    
        Evento evento = this.eventoRepository.findBySedeId(UUID.fromString(command.sedeId())).orElseThrow(() -> new EventoNotFoundException("No se encontró un evento asociado a la sede con ID: " + command.sedeId()));

        evento.sinEventoPublicadoPendiente();

        this.sedeRepository.delete(sede);

    }
}
