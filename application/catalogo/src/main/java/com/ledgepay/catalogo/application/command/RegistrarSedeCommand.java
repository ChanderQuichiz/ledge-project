package com.ledgepay.catalogo.application.command;

public record RegistrarSedeCommand(
String nombre,
String direccion,
String ciudad,
Integer aforoMaximo
) {
    
}
