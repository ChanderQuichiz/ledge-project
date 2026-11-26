package com.ledgepay.catalogo.presentation.rest.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ledgepay.catalogo.application.exceptions.EntradasConsultaException;
import com.ledgepay.catalogo.application.exceptions.DesactivacionSedeInvalida;
import com.ledgepay.catalogo.application.exceptions.PreventaVigenteException;
import com.ledgepay.catalogo.application.exceptions.SedeNotFoundException;
import com.ledgepay.catalogo.domain.exceptions.AforoInsuficienteException;
import com.ledgepay.catalogo.domain.exceptions.AsientosDuplicadosException;
import com.ledgepay.catalogo.domain.exceptions.EventoNotFoundException;
import com.ledgepay.catalogo.domain.exceptions.EventoPublicadoPendienteException;
import com.ledgepay.catalogo.domain.exceptions.HorasPreviasInvalidaException;
import com.ledgepay.catalogo.domain.exceptions.RangoFechaInvalidoException;
import com.ledgepay.catalogo.domain.exceptions.ZonaDuplicadaException;
import com.ledgepay.catalogo.domain.exceptions.ZonaNotFoundException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SedeNotFoundException.class)
    ProblemDetail sedeNoEncontrada(SedeNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Sede no encontrada", exception.getMessage(), "SEDE_NO_ENCONTRADA");
    }

    @ExceptionHandler(ZonaNotFoundException.class)
    ProblemDetail zonaNoEncontrada(ZonaNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Zona no encontrada", exception.getMessage(), "ZONA_NO_ENCONTRADA");
    }

    @ExceptionHandler(EventoNotFoundException.class)
    ProblemDetail eventoNoEncontrado(EventoNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Evento no encontrado", exception.getMessage(), "EVENTO_NO_ENCONTRADO");
    }

    @ExceptionHandler(ZonaDuplicadaException.class)
    ProblemDetail zonaDuplicada(ZonaDuplicadaException exception) {
        return problem(HttpStatus.CONFLICT, "Zona duplicada", exception.getMessage(), "ZONA_DUPLICADA");
    }

    @ExceptionHandler(AsientosDuplicadosException.class)
    ProblemDetail asientosDuplicados(AsientosDuplicadosException exception) {
        return problem(HttpStatus.CONFLICT, "Asiento duplicado", exception.getMessage(), "ASIENTO_DUPLICADO");
    }

    @ExceptionHandler(EventoPublicadoPendienteException.class)
    ProblemDetail eventoPublicadoPendiente(EventoPublicadoPendienteException exception) {
        String mensaje = exception.getMessage();
        String codigo = mensaje != null && mensaje.contains("zona")
            ? "ZONA_CON_EVENTOS_FUTUROS"
            : "SEDE_CON_EVENTOS_FUTUROS";
        return problem(HttpStatus.CONFLICT, "Recurso con eventos futuros", mensaje, codigo);
    }

    @ExceptionHandler(DesactivacionSedeInvalida.class)
    ProblemDetail desactivacionInvalida(DesactivacionSedeInvalida exception) {
        return problem(HttpStatus.CONFLICT, "No se puede desactivar", exception.getMessage(), "SEDE_CON_VENTAS");
    }

    @ExceptionHandler(PreventaVigenteException.class)
    ProblemDetail preventaVigente(PreventaVigenteException exception) {
        return problem(HttpStatus.CONFLICT, "Preventa vigente", exception.getMessage(), "PREVENTA_VIGENTE");
    }

    @ExceptionHandler(AforoInsuficienteException.class)
    ProblemDetail aforoInsuficiente(AforoInsuficienteException exception) {
        String mensaje = exception.getMessage();
        String codigo = mensaje != null && mensaje.contains("capacidad disponible")
            ? "AFORO_EXCEDIDO"
            : "AFORO_INVALIDO";
        return problem(HttpStatus.UNPROCESSABLE_CONTENT, "Aforo inválido", mensaje, codigo);
    }

    @ExceptionHandler({ HorasPreviasInvalidaException.class, RangoFechaInvalidoException.class })
    ProblemDetail fechasInvalidas(RuntimeException exception) {
        return problem(HttpStatus.UNPROCESSABLE_CONTENT, "Fechas inválidas", exception.getMessage(), "FECHAS_INVALIDAS");
    }

    @ExceptionHandler({ ConstraintViolationException.class, IllegalArgumentException.class })
    ProblemDetail solicitudInvalida(Exception exception) {
        return problem(HttpStatus.BAD_REQUEST, "Solicitud inválida", "La solicitud contiene datos inválidos",
            "SOLICITUD_INVALIDA");
    }

    @ExceptionHandler(EntradasConsultaException.class)
    ProblemDetail entradasNoDisponibles(EntradasConsultaException exception) {
        return problem(HttpStatus.BAD_GATEWAY, "Servicio de entradas no disponible",
            "No se pudo consultar las entradas del evento", "ENTRADAS_NO_DISPONIBLES");
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail errorInterno(Exception exception) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", "Ocurrió un error inesperado",
            "ERROR_INTERNO");
    }

    private ProblemDetail problem(HttpStatus status, String titulo, String detalle, String codigo) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detalle);
        problemDetail.setTitle(titulo);
        problemDetail.setProperty("code", codigo);
        return problemDetail;
    }
}
