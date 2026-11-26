package com.ledgepay.catalogo.infrastructure.persistence.jpa.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ledgepay.catalogo.domain.value_objects.Fase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fases_venta", indexes = @Index(name = "ix_fases_venta_evento_fase", columnList = "evento_id, fase", unique = true))
@Getter
@Setter
@NoArgsConstructor
public class FaseVentaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "evento_id", nullable = false)
    private UUID eventoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "fase", nullable = false)
    private Fase fase;

    @Column(name = "inicia_en", nullable = false)
    private LocalDateTime iniciaEn;

    @Column(name = "termina_en", nullable = false)
    private LocalDateTime terminaEn;

    @Column(name = "acceso_restringido", nullable = false)
    private Boolean accesoRestringido;

    @Column(name = "codigo_acceso_hash", length = 128)
    private String codigoAccesoHash;
}
