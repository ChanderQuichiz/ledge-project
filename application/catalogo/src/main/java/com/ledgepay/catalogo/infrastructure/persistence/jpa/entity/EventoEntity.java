package com.ledgepay.catalogo.infrastructure.persistence.jpa.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ledgepay.catalogo.domain.value_objects.EstadoEvento;
import com.ledgepay.catalogo.domain.value_objects.PoliticaCancelacion;
import com.ledgepay.catalogo.domain.value_objects.RestriccionEdad;

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
@Table(name = "eventos", indexes = {
        @Index(name = "ix_eventos_sede_horario", columnList = "sede_id, fecha_inicio, fecha_fin"),
        @Index(name = "ix_eventos_organizador_estado", columnList = "organizador_id, estado")
})
@Getter
@Setter
@NoArgsConstructor
public class EventoEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "organizador_id", nullable = false)
    private UUID organizadorId;

    @Column(name = "sede_id", nullable = false)
    private UUID sedeId;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "text")
    private String descripcion;

    @Column(name = "categoria", nullable = false, length = 60)
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "restriccion_edad", nullable = false)
    private RestriccionEdad restriccionEdad;

    @Enumerated(EnumType.STRING)
    @Column(name = "politica_cancelacion", nullable = false)
    private PoliticaCancelacion politicaCancelacion;

    @Column(name = "permite_reingreso", nullable = false)
    private Boolean permiteReingreso;

    @Column(name = "max_reingresos")
    private Integer maxReingresos;

    @Column(name = "es_nominativo", nullable = false)
    private Boolean esNominativo;

    @Column(name = "es_exonerado", nullable = false)
    private Boolean esExonerado;

    @Column(name = "limite_compra_por_cliente", nullable = false)
    private Integer limiteCompraPorCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoEvento estado;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "apertura_puertas", nullable = false)
    private LocalDateTime aperturaPuertas;

    @Column(name = "inicio_venta")
    private LocalDateTime inicioVenta;

    @Column(name = "fin_venta")
    private LocalDateTime finVenta;

    @Column(name = "cargo_servicio_porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal cargoServicioPorcentaje;

    @Column(name = "cargo_servicio_minimo", nullable = false, precision = 12, scale = 2)
    private BigDecimal cargoServicioMinimo;

    @Column(name = "cargo_servicio_maximo", nullable = false, precision = 12, scale = 2)
    private BigDecimal cargoServicioMaximo;

    @Column(name = "porcentaje_comision", precision = 4, scale = 2)
    private BigDecimal porcentajeComision;

    @Column(name = "numero_reprogramaciones", nullable = false)
    private Integer numeroReprogramaciones;

    @Column(name = "publicado_en")
    private LocalDateTime publicadoEn;

    @Column(name = "cancelado_en")
    private LocalDateTime canceladoEn;

    @Column(name = "motivo_cancelacion", columnDefinition = "text")
    private String motivoCancelacion;

    @Column(name = "penalizacion_cancelacion_porcentaje", nullable = false, precision = 4, scale = 2)
    private BigDecimal penalizacionCancelacionPorcentaje;

    @Column(name = "creado_por", nullable = false)
    private UUID creadoPor;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
}
