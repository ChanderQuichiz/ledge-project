package com.ledgepay.catalogo.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ledgepay.catalogo.domain.value_objects.EstadoOrganizador;

public class Organizador {
    private UUID id;
    private String ruc;
    private String razonSocial;
    private EstadoOrganizador estado;
    private BigDecimal porcentajeComision;
    private UUID verificadoPor;
    private LocalDateTime verificadoEn;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
	public UUID getId() {
		return id;
	}
	public String getRuc() {
		return ruc;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public EstadoOrganizador getEstado() {
		return estado;
	}
	public BigDecimal getPorcentajeComision() {
		return porcentajeComision;
	}
	public UUID getVerificadoPor() {
		return verificadoPor;
	}
	public LocalDateTime getVerificadoEn() {
		return verificadoEn;
	}
	public LocalDateTime getCreadoEn() {
		return creadoEn;
	}
	public LocalDateTime getActualizadoEn() {
		return actualizadoEn;
	}
}

