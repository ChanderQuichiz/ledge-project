package com.ledgepay.catalogo.application.result;

import java.util.List;

public record EntradasByEventoResult(
    List<Entrada> entradas
) {
    public record Entrada(
    String id,
    String codigo,
    String reservaId,
    String eventoId,
    String zonaId,
    String asientoId,
    Integer posicion,
    Integer version,
    String estado,
    Boolean esCortesia,
    String compradorId,
    String titularId,
    String titularNombre,
    String titularDocumentoTipo,
    String titularDocumentoNumero,
    Integer transferenciasPrevias,
    Boolean correccionNominativaAplicada,
    String entradaAnteriorId,
    Boolean suspendida,
    String vigenteHasta,
    String motivoAnulacion,
    String emitidaEn,
    String anuladaEn
    ){};
}
