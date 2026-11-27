package com.ledgepay.catalogo.infrastructure.persistence.jpa.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ledgepay.catalogo.domain.AsientoZona;
import com.ledgepay.catalogo.domain.Evento;
import com.ledgepay.catalogo.domain.FaseVenta;
import com.ledgepay.catalogo.domain.Organizador;
import com.ledgepay.catalogo.domain.PuntoAccesoSede;
import com.ledgepay.catalogo.domain.PuntoAccesoZona;
import com.ledgepay.catalogo.domain.Sede;
import com.ledgepay.catalogo.domain.ZonaSede;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.entity.AsientoZonaEntity;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.entity.EventoEntity;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.entity.FaseVentaEntity;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.entity.OrganizadorEntity;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.entity.PuntoAccesoSedeEntity;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.entity.PuntoAccesoZonaEntity;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.entity.SedeEntity;
import com.ledgepay.catalogo.infrastructure.persistence.jpa.entity.ZonaSedeEntity;

public final class PersistenceMapper {

    private static final BigDecimal CARGO_PORCENTAJE_DEFAULT = new BigDecimal("10.00");
    private static final BigDecimal CARGO_MINIMO_DEFAULT = new BigDecimal("2.00");
    private static final BigDecimal CARGO_MAXIMO_DEFAULT = new BigDecimal("15.00");
    private static final BigDecimal PENALIZACION_DEFAULT = new BigDecimal("0.00");

    private PersistenceMapper() {
    }

    public static Sede toDomain(SedeEntity entity) {
        if (entity == null) {
            return null;
        }
        List<ZonaSede> zonas = entity.getZonas().stream().map(PersistenceMapper::toDomain).toList();
        List<PuntoAccesoSede> puntosAcceso = entity.getPuntosAcceso().stream()
            .map(PersistenceMapper::toDomain)
            .toList();
        return new Sede(entity.getId(), entity.getNombre(), entity.getDireccion(), entity.getCiudad(),
            entity.getAforoMaximoLegal(), entity.getActiva(), entity.getCreadoEn(), zonas, puntosAcceso);
    }

    public static SedeEntity toEntity(Sede domain) {
        if (domain == null) {
            return null;
        }
        SedeEntity entity = new SedeEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setDireccion(domain.getDireccion());
        entity.setCiudad(domain.getCiudad());
        entity.setAforoMaximoLegal(domain.getAforoMaximoLegal());
        entity.setActiva(domain.getActiva() != null ? domain.getActiva() : true);
        entity.setCreadoEn(domain.getCreadoEn());

        for (PuntoAccesoSede punto : domain.getPuntosAcceso()) {
            entity.getPuntosAcceso().add(toEntity(punto, entity));
        }
        for (ZonaSede zona : domain.getZonas()) {
            entity.getZonas().add(toEntity(zona, entity));
        }
        return entity;
    }

    public static void updateEntity(SedeEntity entity, Sede domain) {
        entity.setNombre(domain.getNombre());
        entity.setDireccion(domain.getDireccion());
        entity.setCiudad(domain.getCiudad());
        entity.setAforoMaximoLegal(domain.getAforoMaximoLegal());
        entity.setActiva(domain.getActiva() != null ? domain.getActiva() : true);
        entity.setCreadoEn(domain.getCreadoEn());

        entity.getZonas().clear();
        for (ZonaSede zona : domain.getZonas()) {
            entity.getZonas().add(toEntity(zona, entity));
        }
        entity.getPuntosAcceso().clear();
        for (PuntoAccesoSede punto : domain.getPuntosAcceso()) {
            entity.getPuntosAcceso().add(toEntity(punto, entity));
        }
    }

    public static ZonaSede toDomain(ZonaSedeEntity entity) {
        if (entity == null) {
            return null;
        }
        List<AsientoZona> asientos = entity.getAsientos().stream().map(PersistenceMapper::toDomain).toList();
        List<PuntoAccesoZona> puntosAcceso = entity.getPuntosAccesoZonas().stream()
            .map(PersistenceMapper::toDomain)
            .toList();
        return ZonaSede.reconstruir(entity.getId(), entity.getSede().getId(), entity.getNombre(), entity.getTipo(),
            entity.getCapacidad(), entity.getActiva(), asientos, puntosAcceso);
    }

    public static ZonaSedeEntity toEntity(ZonaSede domain, SedeEntity sede) {
        if (domain == null) {
            return null;
        }
        ZonaSedeEntity entity = new ZonaSedeEntity();
        entity.setId(domain.getId());
        entity.setSede(sede);
        entity.setNombre(domain.getNombre());
        entity.setTipo(domain.getTipo());
        entity.setCapacidad(domain.getCapacidad());
        entity.setActiva(domain.getActiva() != null ? domain.getActiva() : true);

        for (AsientoZona asiento : domain.getAsientos()) {
            entity.getAsientos().add(toEntity(asiento, entity));
        }
        for (PuntoAccesoZona puntoAcceso : domain.getPuntosAcceso()) {
            entity.getPuntosAccesoZonas().add(toEntity(puntoAcceso, entity, sede));
        }
        return entity;
    }

    public static void updateEntity(ZonaSedeEntity entity, ZonaSede domain) {
        entity.setNombre(domain.getNombre());
        entity.setTipo(domain.getTipo());
        entity.setCapacidad(domain.getCapacidad());
        entity.setActiva(domain.getActiva() != null ? domain.getActiva() : true);

        entity.getAsientos().clear();
        for (AsientoZona asiento : domain.getAsientos()) {
            entity.getAsientos().add(toEntity(asiento, entity));
        }
        entity.getPuntosAccesoZonas().clear();
        for (PuntoAccesoZona puntoAcceso : domain.getPuntosAcceso()) {
            entity.getPuntosAccesoZonas().add(toEntity(puntoAcceso, entity, entity.getSede()));
        }
    }

    public static AsientoZona toDomain(AsientoZonaEntity entity) {
        if (entity == null) {
            return null;
        }
        return AsientoZona.reconstruir(entity.getId(), entity.getZona().getId(), entity.getFila(),
            entity.getNumero(), entity.getAccesible(), entity.getHabilitado(), entity.getMotivoInhabilitacion());
    }

    public static AsientoZonaEntity toEntity(AsientoZona domain, ZonaSedeEntity zona) {
        if (domain == null) {
            return null;
        }
        AsientoZonaEntity entity = new AsientoZonaEntity();
        entity.setId(domain.getId());
        entity.setZona(zona);
        entity.setFila(domain.getFila());
        entity.setNumero(domain.getNumero());
        entity.setAccesible(domain.getAccesible() != null ? domain.getAccesible() : false);
        entity.setHabilitado(domain.getHabilitado() != null ? domain.getHabilitado() : true);
        entity.setMotivoInhabilitacion(domain.getMotivoInhabilitacion());
        return entity;
    }

    public static PuntoAccesoSede toDomain(PuntoAccesoSedeEntity entity) {
        if (entity == null) {
            return null;
        }
        return PuntoAccesoSede.reconstruir(entity.getId(), entity.getSede().getId(), entity.getNombre());
    }

    public static PuntoAccesoSedeEntity toEntity(PuntoAccesoSede domain, SedeEntity sede) {
        if (domain == null) {
            return null;
        }
        PuntoAccesoSedeEntity entity = new PuntoAccesoSedeEntity();
        entity.setId(domain.getId());
        entity.setSede(sede);
        entity.setNombre(domain.getNombre());
        return entity;
    }

    public static PuntoAccesoZona toDomain(PuntoAccesoZonaEntity entity) {
        if (entity == null || entity.getId() == null) {
            return null;
        }
        return PuntoAccesoZona.reconstruir(entity.getId().getPuntoAccesoId(), entity.getId().getZonaId(), null, null);
    }

    public static PuntoAccesoZonaEntity toEntity(PuntoAccesoZona domain, ZonaSedeEntity zona, SedeEntity sede) {
        if (domain == null) {
            return null;
        }
        PuntoAccesoSedeEntity puntoAcceso = sede.getPuntosAcceso().stream()
            .filter(punto -> punto.getId().equals(domain.getPuntoAccesoId()))
            .findFirst()
            .orElseGet(() -> {
                PuntoAccesoSedeEntity stub = new PuntoAccesoSedeEntity();
                stub.setId(domain.getPuntoAccesoId());
                stub.setSede(sede);
                stub.setNombre("");
                return stub;
            });
        return new PuntoAccesoZonaEntity(puntoAcceso, zona);
    }

    public static Evento toDomain(EventoEntity entity) {
        if (entity == null) {
            return null;
        }
        return Evento.reconstruir(entity.getId(), entity.getOrganizadorId(), entity.getSedeId(), entity.getNombre(),
            entity.getDescripcion(), entity.getCategoria(), entity.getRestriccionEdad(),
            entity.getPoliticaCancelacion(), entity.getPermiteReingreso(), entity.getMaxReingresos(),
            entity.getEsNominativo(), entity.getEsExonerado(), entity.getLimiteCompraPorCliente(), entity.getEstado(),
            entity.getFechaInicio(), entity.getFechaFin(), entity.getAperturaPuertas(), entity.getInicioVenta(),
            entity.getFinVenta(), entity.getCargoServicioPorcentaje(), entity.getCargoServicioMinimo(),
            entity.getCargoServicioMaximo(), entity.getPorcentajeComision(), entity.getNumeroReprogramaciones(),
            entity.getPublicadoEn(), entity.getCanceladoEn(), entity.getMotivoCancelacion(),
            entity.getPenalizacionCancelacionPorcentaje(), entity.getCreadoPor(), entity.getCreadoEn(),
            entity.getActualizadoEn(), null, null, new ArrayList<>());
    }

    public static EventoEntity toEntity(Evento domain) {
        if (domain == null) {
            return null;
        }
        EventoEntity entity = new EventoEntity();
        entity.setId(domain.getId());
        entity.setOrganizadorId(domain.getOrganizadorId());
        entity.setSedeId(domain.getSedeId());
        entity.setNombre(domain.getNombre());
        entity.setDescripcion(domain.getDescripcion());
        entity.setCategoria(domain.getCategoria());
        entity.setRestriccionEdad(domain.getRestriccionEdad());
        entity.setPoliticaCancelacion(domain.getPoliticaCancelacion());
        entity.setPermiteReingreso(domain.getPermiteReingreso() != null ? domain.getPermiteReingreso() : false);
        entity.setMaxReingresos(domain.getMaxReingresos());
        entity.setEsNominativo(domain.getEsNominativo() != null ? domain.getEsNominativo() : false);
        entity.setEsExonerado(domain.getEsExonerado() != null ? domain.getEsExonerado() : false);
        entity.setLimiteCompraPorCliente(domain.getLimiteCompraPorCliente() != null ? domain.getLimiteCompraPorCliente() : 6);
        entity.setEstado(domain.getEstado());
        entity.setFechaInicio(domain.getFechaInicio());
        entity.setFechaFin(domain.getFechaFin());
        entity.setAperturaPuertas(domain.getAperturaPuertas() != null ? domain.getAperturaPuertas() : domain.getFechaInicio());
        entity.setInicioVenta(domain.getInicioVenta());
        entity.setFinVenta(domain.getFinVenta());
        entity.setCargoServicioPorcentaje(valueOrDefault(domain.getCargoServicioPorcentaje(), CARGO_PORCENTAJE_DEFAULT));
        entity.setCargoServicioMinimo(valueOrDefault(domain.getCargoServicioMinimo(), CARGO_MINIMO_DEFAULT));
        entity.setCargoServicioMaximo(valueOrDefault(domain.getCargoServicioMaximo(), CARGO_MAXIMO_DEFAULT));
        entity.setPorcentajeComision(domain.getPorcentajeComision());
        entity.setNumeroReprogramaciones(domain.getNumeroReprogramaciones() != null ? domain.getNumeroReprogramaciones() : 0);
        entity.setPublicadoEn(domain.getPublicadoEn());
        entity.setCanceladoEn(domain.getCanceladoEn());
        entity.setMotivoCancelacion(domain.getMotivoCancelacion());
        entity.setPenalizacionCancelacionPorcentaje(
            valueOrDefault(domain.getPenalizacionCancelacionPorcentaje(), PENALIZACION_DEFAULT));
        entity.setCreadoPor(domain.getCreadoPor());
        entity.setCreadoEn(domain.getCreadoEn());
        entity.setActualizadoEn(domain.getActualizadoEn());
        return entity;
    }

    public static FaseVenta toDomain(FaseVentaEntity entity) {
        if (entity == null) {
            return null;
        }
        return FaseVenta.reconstruir(entity.getId(), entity.getEventoId(), entity.getFase(), entity.getIniciaEn(),
            entity.getTerminaEn(), entity.getAccesoRestringido(), entity.getCodigoAccesoHash());
    }

    public static FaseVentaEntity toEntity(FaseVenta domain) {
        if (domain == null) {
            return null;
        }
        FaseVentaEntity entity = new FaseVentaEntity();
        entity.setId(domain.getId());
        entity.setEventoId(domain.getEventoId());
        entity.setFase(domain.getFase());
        entity.setIniciaEn(domain.getIniciaEn());
        entity.setTerminaEn(domain.getTerminaEn());
        entity.setAccesoRestringido(domain.getAccesoRestringido() != null ? domain.getAccesoRestringido() : false);
        entity.setCodigoAccesoHash(domain.getCodigoAccesoHash());
        return entity;
    }

    public static Organizador toDomain(OrganizadorEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Organizador(entity.getId(), entity.getRuc(), entity.getRazonSocial(), entity.getEstado(),
            entity.getPorcentajeComision(), entity.getVerificadoPor(), entity.getVerificadoEn(),
            entity.getCreadoEn(), entity.getActualizadoEn());
    }

    private static BigDecimal valueOrDefault(BigDecimal value, BigDecimal defaultValue) {
        return value != null ? value : defaultValue;
    }
}
