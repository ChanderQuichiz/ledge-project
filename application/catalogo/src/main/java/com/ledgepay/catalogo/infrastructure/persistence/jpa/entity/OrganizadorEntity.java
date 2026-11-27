package com.ledgepay.catalogo.infrastructure.persistence.jpa.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ledgepay.catalogo.domain.value_objects.EstadoOrganizador;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organizadores")
@Getter
@Setter
@NoArgsConstructor
public class OrganizadorEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "ruc", nullable = false, length = 11)
    private String ruc;

    @Column(name = "razon_social", nullable = false, length = 200)
    private String razonSocial;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoOrganizador estado;

    @Column(name = "porcentaje_comision", nullable = false)
    private java.math.BigDecimal porcentajeComision;

    @Column(name = "verificado_por")
    private UUID verificadoPor;

    @Column(name = "verificado_en")
    private LocalDateTime verificadoEn;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;
}
