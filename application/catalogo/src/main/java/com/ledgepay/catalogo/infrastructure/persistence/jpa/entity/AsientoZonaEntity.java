package com.ledgepay.catalogo.infrastructure.persistence.jpa.entity;

import java.util.UUID;

import com.ledgepay.catalogo.domain.value_objects.MotivoInhabilitacionAsiento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "asientos_zona", uniqueConstraints = @UniqueConstraint(name = "uk_asientos_zona_zona_fila_numero", columnNames = { "zona_id", "fila", "numero" }))
@Getter
@Setter
@NoArgsConstructor
public class AsientoZonaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zona_id", nullable = false)
    private ZonaSedeEntity zona;

    @Column(name = "fila", nullable = false, length = 10)
    private String fila;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "accesible", nullable = false)
    private Boolean accesible;

    @Column(name = "habilitado", nullable = false)
    private Boolean habilitado;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_inhabilitacion")
    private MotivoInhabilitacionAsiento motivoInhabilitacion;
}
