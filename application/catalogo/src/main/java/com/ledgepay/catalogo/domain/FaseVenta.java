package com.ledgepay.catalogo.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ledgepay.catalogo.domain.value_objects.Fase;

public class FaseVenta{
    private UUID id;
    private UUID eventoId;
    private Fase fase;
    private LocalDateTime iniciaEn;
    private LocalDateTime terminaEn;
    private Boolean accesoRestringido;
    private String codigoAccesoHash;



    public UUID getId() {
        return id;
    }
    public UUID getEventoId() {
        return eventoId;
    }
    public Fase getFase() {
        return fase;
    }
    public LocalDateTime getIniciaEn() {
        return iniciaEn;
    }
    public LocalDateTime getTerminaEn() {
        return terminaEn;
    }
    public Boolean getAccesoRestringido() {
        return accesoRestringido;
    }
    public String getCodigoAccesoHash() {
        return codigoAccesoHash;
    }


    public void iniciarVentaGeneral() {
        this.fase = Fase.GENERAL;
    }

    public static FaseVenta reconstruir(UUID id, UUID eventoId, Fase fase, LocalDateTime iniciaEn,
            LocalDateTime terminaEn, Boolean accesoRestringido, String codigoAccesoHash) {
        FaseVenta faseVenta = new FaseVenta();
        faseVenta.id = id;
        faseVenta.eventoId = eventoId;
        faseVenta.fase = fase;
        faseVenta.iniciaEn = iniciaEn;
        faseVenta.terminaEn = terminaEn;
        faseVenta.accesoRestringido = accesoRestringido;
        faseVenta.codigoAccesoHash = codigoAccesoHash;
        return faseVenta;
    }

}

