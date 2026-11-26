package com.ledgepay.catalogo.application.result;


public record RegistrarSedeResult(
    String sedeId,
    String nombre,
    String direccion,
    String ciudad,
    Integer aforoMaximo,
    String estado
) {
   
    
}