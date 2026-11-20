package com.ledgepay.catalogo.application.usecases;

import com.ledgepay.catalogo.application.command.RegistrarSedeCommand;
import com.ledgepay.catalogo.application.result.RegistrarSedeResult;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.repositories.SedeRepository;

public class RegistrarSedeUseCase {

private final SedeRepository sedeRepository;
public RegistrarSedeUseCase(SedeRepository sedeRepository) {
        this.sedeRepository = sedeRepository;
    }

    public RegistrarSedeResult execute(RegistrarSedeCommand command) {
        // Lógica para registrar la sede
  
        Sede sede = Sede.RegisterSede(command.nombre(), command.direccion(), command.ciudad(), command.aforoMaximo());

        

       Sede savedSede = this.sedeRepository.save(sede); // Guardar la nueva sede en la base de datos
        // Obtener el estado de la sede
        return new RegistrarSedeResult(
            savedSede.getId().toString(),
            command.nombre(),
            command.direccion(),
            command.ciudad(),
            command.aforoMaximo(),
            savedSede.getActiva() ? "Activa" : "Inactiva"
        );
    }
}




