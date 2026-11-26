package com.ledgepay.catalogo.infrastructure.persistence.jpa.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "puntos_acceso_zonas")
@Getter
@Setter
@NoArgsConstructor
public class PuntoAccesoZonaEntity {

    @EmbeddedId
    private PuntoAccesoZonaId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("puntoAccesoId")
    @JoinColumn(name = "punto_acceso_id", nullable = false)
    private PuntoAccesoSedeEntity puntoAcceso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("zonaId")
    @JoinColumn(name = "zona_id", nullable = false)
    private ZonaSedeEntity zona;

    public PuntoAccesoZonaEntity(PuntoAccesoSedeEntity puntoAcceso, ZonaSedeEntity zona) {
        this.puntoAcceso = puntoAcceso;
        this.zona = zona;
        this.id = new PuntoAccesoZonaId(puntoAcceso.getId(), zona.getId());
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class PuntoAccesoZonaId implements Serializable {

        @Column(name = "punto_acceso_id", nullable = false)
        private UUID puntoAccesoId;

        @Column(name = "zona_id", nullable = false)
        private UUID zonaId;
    }
}
