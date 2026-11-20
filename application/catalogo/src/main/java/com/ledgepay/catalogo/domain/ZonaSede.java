package com.ledgepay.catalogo.domain;
import java.util.List;
import java.util.UUID;

import com.ledgepay.catalogo.domain.exceptions.AsientosDuplicadosException;
import com.ledgepay.catalogo.domain.value_objects.TipoZona;

public class ZonaSede {
    private UUID id;
    private UUID sedeId;
    private String nombre;
    private TipoZona tipo;
    private Integer capacidad;
    private Boolean activa;

	private List<AsientoZona> asientos;
	private List<PuntoAccesoZona> puntosAcceso;

	public UUID getId() {
		return id;
	}
	public List<AsientoZona> getAsientos() {
		return asientos;
	}
	public UUID getSedeId() {
		return sedeId;
	}



	public String getNombre() {
		return nombre;
	}

	public TipoZona getTipo() {
		return tipo;
	}

	public Integer getCapacidad() {
		return capacidad;
	}

	public Boolean getActiva() {
		return activa;
	}


public void desactivar() {
		this.activa = false;
	}


	public List<PuntoAccesoZona> getPuntosAcceso() {
		return puntosAcceso;
	}




	public void unicidadDeAsientos(){
		this.asientos.stream()
		.map(asiento -> asiento.getFila() + "-" + asiento.getNumero())
		.distinct()
		.forEach(asiento -> {
			long count = this.asientos.stream()
			.filter(a -> (a.getFila() + "-" + a.getNumero()).equals(asiento))
			.count();
			if(count > 1){
				throw new AsientosDuplicadosException("La zona tiene asientos duplicados: " + asiento);
			}
		});
	}


	public static ZonaSede crear(UUID sedeId, String nombre, TipoZona tipo, Integer capacidad) {
		ZonaSede zona = new ZonaSede();
		zona.id = UUID.randomUUID();
		zona.sedeId = sedeId;
		zona.nombre = nombre;
		zona.tipo = tipo;
		zona.capacidad = capacidad;
		zona.activa = true;

		return zona;
	}


	public void definirAsientos(List<AsientoZona> asientos) {
		this.asientos = asientos;
		unicidadDeAsientos();
	}

}
