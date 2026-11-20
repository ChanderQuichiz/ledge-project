package com.ledgepay.catalogo.domain;

import java.util.UUID;

import com.ledgepay.catalogo.domain.value_objects.MotivoInhabilitacionAsiento;

public class AsientoZona {
    private UUID id;
    private UUID zonaId;
    private String fila;
    private Integer numero;
    private Boolean accesible;
    private Boolean habilitado;
    private MotivoInhabilitacionAsiento motivoInhabilitacion;
    
private AsientoZona(UUID id, UUID zonaId, String fila, Integer numero, Boolean accesible, Boolean habilitado) {
		this.id = id;
		this.zonaId = zonaId;
		this.fila = fila;
		this.numero = numero;
		this.accesible = accesible;
		this.habilitado = habilitado;
	}
	public static AsientoZona crear( UUID zonaId, String fila, Integer numero, Boolean accesible, Boolean habilitado) {
		return new AsientoZona(UUID.randomUUID(), zonaId, fila, numero, accesible, habilitado);
	}
    public UUID getId() {
		return id;
	}
	public UUID getZonaId() {
		return zonaId;
	}
	public String getFila() {
		return fila;
	}
	public Integer getNumero() {
		return numero;
	}
	public Boolean getAccesible() {
		return accesible;
	}
	public Boolean getHabilitado() {
		return habilitado;
	}
	public MotivoInhabilitacionAsiento getMotivoInhabilitacion() {
		return motivoInhabilitacion;
	}
}
