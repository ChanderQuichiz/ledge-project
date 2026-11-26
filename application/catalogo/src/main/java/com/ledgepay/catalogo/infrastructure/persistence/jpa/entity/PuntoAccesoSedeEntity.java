package com.ledgepay.catalogo.infrastructure.persistence.jpa.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "puntos_acceso_sede", uniqueConstraints = @UniqueConstraint(name = "uk_puntos_acceso_sede_sede_nombre", columnNames = { "sede_id", "nombre" }))
@Getter
@Setter
@NoArgsConstructor
public class PuntoAccesoSedeEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sede_id", nullable = false)
    private SedeEntity sede;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
}
