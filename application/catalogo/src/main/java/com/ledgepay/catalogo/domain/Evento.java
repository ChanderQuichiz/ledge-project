package com.ledgepay.catalogo.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.ledgepay.catalogo.domain.exceptions.EventoPublicadoPendienteException;
import com.ledgepay.catalogo.domain.exceptions.HorasPreviasInvalidaException;
import com.ledgepay.catalogo.domain.exceptions.RangoFechaInvalidoException;
import com.ledgepay.catalogo.domain.value_objects.EstadoEvento;
import com.ledgepay.catalogo.domain.value_objects.PoliticaCancelacion;
import com.ledgepay.catalogo.domain.value_objects.RestriccionEdad;

public class Evento {
    private final UUID id;
    private UUID organizadorId;
    private UUID sedeId;
    private String nombre;
    private String descripcion;
    private String categoria;
    private RestriccionEdad restriccionEdad;
    private PoliticaCancelacion politicaCancelacion;
    private Boolean permiteReingreso;
    private Integer maxReingresos;
    private Boolean esNominativo;
    private Boolean esExonerado;
    private Integer limiteCompraPorCliente;
    private EstadoEvento estado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime aperturaPuertas;
    private LocalDateTime inicioVenta;
    private LocalDateTime finVenta;
    private BigDecimal cargoServicioPorcentaje;
    private BigDecimal cargoServicioMinimo;
    private BigDecimal cargoServicioMaximo;
    private BigDecimal porcentajeComision;
    private Integer numeroReprogramaciones;
    private LocalDateTime publicadoEn;
    private LocalDateTime canceladoEn;
    private String motivoCancelacion;
    private BigDecimal penalizacionCancelacionPorcentaje;
    private final UUID creadoPor;
    private final LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

  private Organizador organizador;
  private Sede sede;
    private List<FaseVenta> fasesVenta;
    public Organizador getOrganizador() {
    return organizador;
}


public List<FaseVenta> getFasesVenta() {
    return fasesVenta;
  }
  public Sede getSede() {
    return sede;
  }

    // 1. Constructor PRIVADO para creación inicial de negocio
    private Evento(UUID organizadorId, UUID sedeId, String nombre, String descripcion, String categoria,
                   RestriccionEdad restriccionEdad, PoliticaCancelacion politicaCancelacion,
                   Boolean permiteReingreso, Integer maxReingresos, Boolean esNominativo,
                   Boolean esExonerado, Integer limiteCompraPorCliente, LocalDateTime fechaInicio,
                   LocalDateTime fechaFin, LocalDateTime aperturaPuertas, LocalDateTime inicioVenta,
                   LocalDateTime finVenta, UUID creadoPor, LocalDateTime creadoEn) {
        this.id = UUID.randomUUID();
        this.organizadorId = Objects.requireNonNull(organizadorId, "El organizadorId es obligatorio");
        this.sedeId = Objects.requireNonNull(sedeId, "El sedeId es obligatorio");
        this.nombre = Objects.requireNonNull(nombre, "El nombre es obligatorio");
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.restriccionEdad = restriccionEdad;
        this.politicaCancelacion = politicaCancelacion;
        this.permiteReingreso = permiteReingreso != null ? permiteReingreso : false;
        this.maxReingresos = maxReingresos;
        this.esNominativo = esNominativo != null ? esNominativo : false;
        this.esExonerado = esExonerado != null ? esExonerado : false;
        this.limiteCompraPorCliente = limiteCompraPorCliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.aperturaPuertas = aperturaPuertas;
        this.inicioVenta = inicioVenta;
        this.finVenta = finVenta;
        this.creadoPor = Objects.requireNonNull(creadoPor, "El creadoPor es obligatorio");
        this.creadoEn = creadoEn;

        // Invariantes iniciales garantizadas por el dominio
        this.estado = EstadoEvento.BORRADOR;
        this.numeroReprogramaciones = 0;
    }

    // 2. Constructor PRIVADO usado exclusivamente por el Builder de reconstrucción
 
    // 3. Factory Method de Negocio: Punto de entrada único para crear nuevos eventos
    public static Evento registrar(UUID sedeId, UUID organizadorId, String nombre, String descripcion, String categoria,
                                   RestriccionEdad restriccionEdad, PoliticaCancelacion politicaCancelacion,
                                   Boolean permiteReingreso, Integer maxReingresos, Boolean esNominativo,
                                   Boolean esExonerado, Integer limiteCompraPorCliente,
                                   LocalDateTime fechaInicio, LocalDateTime fechaFin, LocalDateTime aperturaPuertas,
                                   LocalDateTime inicioVenta, LocalDateTime finVenta, UUID creadoPor) {

        Objects.requireNonNull(fechaInicio, "La fecha de inicio es obligatoria");
        Objects.requireNonNull(fechaFin, "La fecha de fin es obligatoria");

        LocalDateTime ahora = LocalDateTime.now();

        if (!fechaInicio.minusHours(72).isAfter(ahora)) {
            throw new HorasPreviasInvalidaException(
                "La fecha de inicio del evento debe ser al menos 72 horas después de la fecha de creación del evento."
            );
        }
        if (!fechaFin.isAfter(fechaInicio)) {
            throw new RangoFechaInvalidoException(
                "La fecha de fin del evento debe ser posterior a la fecha de inicio del evento."
            );
        }

        return new Evento(
            organizadorId, sedeId, nombre, descripcion, categoria,
            restriccionEdad, politicaCancelacion, permiteReingreso, maxReingresos,
            esNominativo, esExonerado, limiteCompraPorCliente,
            fechaInicio, fechaFin, aperturaPuertas, inicioVenta, finVenta,
            creadoPor, ahora
        );
    }


    public Boolean preventaVigente(){
           if(LocalDateTime.now().isAfter(this.fechaInicio.minusHours(72))){
               return false;
        }
        return true;
    }
  

    // 4. Punto de entrada para la capa de Infraestructura (Mappers de BD -> Dominio)
  

    private Evento(UUID id, UUID organizadorId, UUID sedeId, String nombre, String descripcion,
                   String categoria, RestriccionEdad restriccionEdad, PoliticaCancelacion politicaCancelacion,
                   Boolean permiteReingreso, Integer maxReingresos, Boolean esNominativo, Boolean esExonerado,
                   Integer limiteCompraPorCliente, EstadoEvento estado, LocalDateTime fechaInicio,
                   LocalDateTime fechaFin, LocalDateTime aperturaPuertas, LocalDateTime inicioVenta,
                   LocalDateTime finVenta, BigDecimal cargoServicioPorcentaje, BigDecimal cargoServicioMinimo,
                   BigDecimal cargoServicioMaximo, BigDecimal porcentajeComision, Integer numeroReprogramaciones,
                   LocalDateTime publicadoEn, LocalDateTime canceladoEn, String motivoCancelacion,
                   BigDecimal penalizacionCancelacionPorcentaje, UUID creadoPor, LocalDateTime creadoEn,
                   LocalDateTime actualizadoEn, Organizador organizador, Sede sede, List<FaseVenta> fasesVenta) {
        this.id = id;
        this.organizadorId = organizadorId;
        this.sedeId = sedeId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.restriccionEdad = restriccionEdad;
        this.politicaCancelacion = politicaCancelacion;
        this.permiteReingreso = permiteReingreso;
        this.maxReingresos = maxReingresos;
        this.esNominativo = esNominativo;
        this.esExonerado = esExonerado;
        this.limiteCompraPorCliente = limiteCompraPorCliente;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.aperturaPuertas = aperturaPuertas;
        this.inicioVenta = inicioVenta;
        this.finVenta = finVenta;
        this.cargoServicioPorcentaje = cargoServicioPorcentaje;
        this.cargoServicioMinimo = cargoServicioMinimo;
        this.cargoServicioMaximo = cargoServicioMaximo;
        this.porcentajeComision = porcentajeComision;
        this.numeroReprogramaciones = numeroReprogramaciones;
        this.publicadoEn = publicadoEn;
        this.canceladoEn = canceladoEn;
        this.motivoCancelacion = motivoCancelacion;
        this.penalizacionCancelacionPorcentaje = penalizacionCancelacionPorcentaje;
        this.creadoPor = creadoPor;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
        this.organizador = organizador;
        this.sede = sede;
        this.fasesVenta = fasesVenta;
    }

    public static Evento reconstruir(UUID id, UUID organizadorId, UUID sedeId, String nombre, String descripcion,
                                     String categoria, RestriccionEdad restriccionEdad,
                                     PoliticaCancelacion politicaCancelacion, Boolean permiteReingreso,
                                     Integer maxReingresos, Boolean esNominativo, Boolean esExonerado,
                                     Integer limiteCompraPorCliente, EstadoEvento estado, LocalDateTime fechaInicio,
                                     LocalDateTime fechaFin, LocalDateTime aperturaPuertas, LocalDateTime inicioVenta,
                                     LocalDateTime finVenta, BigDecimal cargoServicioPorcentaje,
                                     BigDecimal cargoServicioMinimo, BigDecimal cargoServicioMaximo,
                                     BigDecimal porcentajeComision, Integer numeroReprogramaciones,
                                     LocalDateTime publicadoEn, LocalDateTime canceladoEn, String motivoCancelacion,
                                     BigDecimal penalizacionCancelacionPorcentaje, UUID creadoPor,
                                     LocalDateTime creadoEn, LocalDateTime actualizadoEn, Organizador organizador,
                                     Sede sede, List<FaseVenta> fasesVenta) {
        return new Evento(id, organizadorId, sedeId, nombre, descripcion, categoria, restriccionEdad,
            politicaCancelacion, permiteReingreso, maxReingresos, esNominativo, esExonerado,
            limiteCompraPorCliente, estado, fechaInicio, fechaFin, aperturaPuertas, inicioVenta, finVenta,
            cargoServicioPorcentaje, cargoServicioMinimo, cargoServicioMaximo, porcentajeComision,
            numeroReprogramaciones, publicadoEn, canceladoEn, motivoCancelacion,
            penalizacionCancelacionPorcentaje, creadoPor, creadoEn, actualizadoEn, organizador, sede, fasesVenta);
    }

public void sinEventoPublicadoPendiente(){
    
    if(this.estado == EstadoEvento.PUBLICADO && LocalDateTime.now().isBefore(this.fechaInicio)) {
        throw new EventoPublicadoPendienteException("No se puede eliminar la zona porque el evento asociado está publicado o pendiente.");
    }
}



    // --- GETTERS ---

    public UUID getId() { return id; }
    public UUID getOrganizadorId() { return organizadorId; }
    public UUID getSedeId() { return sedeId; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getCategoria() { return categoria; }
    public RestriccionEdad getRestriccionEdad() { return restriccionEdad; }
    public PoliticaCancelacion getPoliticaCancelacion() { return politicaCancelacion; }
    public Boolean getPermiteReingreso() { return permiteReingreso; }
    public Integer getMaxReingresos() { return maxReingresos; }
    public Boolean getEsNominativo() { return esNominativo; }
    public Boolean getEsExonerado() { return esExonerado; }
    public Integer getLimiteCompraPorCliente() { return limiteCompraPorCliente; }
    public EstadoEvento getEstado() { return estado; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public LocalDateTime getFechaFin() { return fechaFin; }
    public LocalDateTime getAperturaPuertas() { return aperturaPuertas; }
    public LocalDateTime getInicioVenta() { return inicioVenta; }
    public LocalDateTime getFinVenta() { return finVenta; }
    public BigDecimal getCargoServicioPorcentaje() { return cargoServicioPorcentaje; }
    public BigDecimal getCargoServicioMinimo() { return cargoServicioMinimo; }
    public BigDecimal getCargoServicioMaximo() { return cargoServicioMaximo; }
    public BigDecimal getPorcentajeComision() { return porcentajeComision; }
    public Integer getNumeroReprogramaciones() { return numeroReprogramaciones; }
    public LocalDateTime getPublicadoEn() { return publicadoEn; }
    public LocalDateTime getCanceladoEn() { return canceladoEn; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
    public BigDecimal getPenalizacionCancelacionPorcentaje() { return penalizacionCancelacionPorcentaje; }
    public UUID getCreadoPor() { return creadoPor; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
}