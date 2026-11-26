package com.ledgepay.catalogo.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import com.ledgepay.catalogo.domain.exceptions.AforoInsuficienteException;
import com.ledgepay.catalogo.domain.exceptions.ZonaDuplicadaException;

public class Sede {
    private UUID id;
    private String nombre;
    private String direccion;
    private String ciudad;
    private Integer aforoMaximoLegal;
    private Boolean activa;
    private LocalDateTime creadoEn;

    private List<ZonaSede> zonas;
    private List<PuntoAccesoSede> puntosAcceso;





	  public List<PuntoAccesoSede> getPuntosAcceso() {
        return puntosAcceso;
    }


      public static Sede RegisterSede(String nombre, String direccion,
        String ciudad,
        Integer aforoMaximoLegal
    ){

        if(aforoMaximoLegal <= 0){
            throw new AforoInsuficienteException("Aforo máximo legal debe ser mayor a cero");
        }

        return new Sede(
            UUID.randomUUID(),
            nombre,
            direccion,
            ciudad,
            aforoMaximoLegal,
            true,
            LocalDateTime.now(),
            List.of(),
            List.of()
        );
    }


public Sede RegistrarZona(ZonaSede zona){


Boolean zonaExiste = this.zonas.stream().anyMatch(z -> z.getNombre().equals(zona.getNombre()));



    Boolean capacidadDisponible =
    this.zonas.stream()
    .mapToInt(z -> z.getCapacidad())
    .sum() + zona.getCapacidad() <= this.aforoMaximoLegal;

    if(!capacidadDisponible ){
        throw new AforoInsuficienteException("No hay capacidad disponible para registrar la zona");
    }
    if(zonaExiste){
        throw new ZonaDuplicadaException("Ya existe una zona con el mismo nombre");
    }


    this.zonas.add(zona);
    return this;
}


public void desactivar() {
    this.activa = false;
}


public Sede(UUID id, String nombre, String direccion, String ciudad, Integer aforoMaximoLegal, Boolean activa, LocalDateTime creadoEn, List<ZonaSede> zonas, List<PuntoAccesoSede> puntosAcceso) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.aforoMaximoLegal = aforoMaximoLegal;
        this.activa = activa;
        this.creadoEn = creadoEn;
        this.zonas = zonas != null ? new ArrayList<>(zonas) : new ArrayList<>();
        this.puntosAcceso = puntosAcceso != null ? new ArrayList<>(puntosAcceso) : new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public Integer getAforoMaximoLegal() {
        return aforoMaximoLegal;
    }

    public Boolean getActiva() {
        return activa;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

          public List<ZonaSede> getZonas() {
		return zonas;
	}




    public void zonasMinimasParaEvento(){
        Integer cantidadZonas = this.zonas.size();
        if(cantidadZonas < 1){
            throw new AforoInsuficienteException("La sede debe tener al menos una zona para poder registrar un evento");
        }
    }
  


}




 