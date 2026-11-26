package com.ledgepay.catalogo.application.usecases;

import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.ledgepay.catalogo.application.command.RecalificarAsientoCommand;
import com.ledgepay.catalogo.application.exceptions.PreventaVigenteException;
import com.ledgepay.catalogo.domain.Evento;
import com.ledgepay.catalogo.domain.FaseVenta;
import com.ledgepay.catalogo.domain.repositories.EventoRepository;
import com.ledgepay.catalogo.domain.repositories.FaseVentaRepository;

public class RecalificarAsientoUseCase {
    
    private final FaseVentaRepository faseVentaRepository;
    private final EventoRepository eventoRepository;
    public RecalificarAsientoUseCase(FaseVentaRepository faseVentaRepository, EventoRepository eventoRepository) {
        this.faseVentaRepository = faseVentaRepository;
        this.eventoRepository = eventoRepository;
    }




    @Transactional
    public void execute(RecalificarAsientoCommand request) {
        Evento evento = eventoRepository.findById(UUID.fromString(request.eventoId()))
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
   

       Boolean vigente = evento.preventaVigente();

       if(vigente){
        throw new PreventaVigenteException("No se puede recalificar asientos mientras la preventa esté vigente");
       }

                FaseVenta faseVenta = faseVentaRepository.findByEventoId(evento.getId());
        faseVenta.iniciarVentaGeneral();
        faseVentaRepository.save(faseVenta);

            }
}
